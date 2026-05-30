package com.sxpi.pan.aimallgateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret:ai-mall-default-secret-key-2024-very-long-key}")
    private String secret;

    // 完全白名单（所有方法都放行）
    private static final List<String> FULL_WHITE_LIST = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh",
            "/api/auth/sms/send",
            "/api/admin/login"
    );

    // GET请求白名单（精确匹配）
    private static final List<String> GET_EXACT_WHITE_LIST = Arrays.asList(
            "/api/products",
            "/api/banners"
    );

    // GET请求白名单（前缀匹配）
    private static final List<String> GET_PREFIX_WHITE_LIST = Arrays.asList(
            "/api/categories",
            "/api/search",
            "/api/products/"  // 只匹配 /api/products/xxx（商品详情），不匹配 /api/products/merchant
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();

        // 完全白名单放行（所有方法）
        for (String whitePath : FULL_WHITE_LIST) {
            if (path.startsWith(whitePath)) {
                return chain.filter(exchange);
            }
        }

        // GET请求白名单放行（精确匹配）
        for (String whitePath : GET_EXACT_WHITE_LIST) {
            if (path.equals(whitePath) && "GET".equals(method)) {
                return chain.filter(exchange);
            }
        }

        // GET请求白名单放行（前缀匹配）
        for (String whitePath : GET_PREFIX_WHITE_LIST) {
            if (path.startsWith(whitePath) && "GET".equals(method)) {
                // 排除 /api/products/merchant
                if (path.equals("/api/products/merchant")) {
                    continue;
                }
                return chain.filter(exchange);
            }
        }

        // 获取token
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        log.debug("AuthFilter path={}, Authorization={}", path, authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("缺少Authorization头, path={}", path);
            return unauthorized(exchange, "未登录或Token已过期");
        }
        String token = authHeader.substring(7);
        log.debug("解析token: {}", token.substring(0, Math.min(20, token.length())) + "...");

        // 验证token
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String userId = claims.get("userId", Long.class).toString();
            String role = claims.get("role", String.class);
            log.debug("token验证成功, userId={}, role={}", userId, role);

            // 将用户信息传递给下游服务
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Role", role)
                    .build();

            log.debug("添加请求头: X-User-Id={}, X-User-Role={}", userId, role);
            log.debug("请求头列表: {}", mutatedRequest.getHeaders().toSingleValueMap());

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception e) {
            log.error("token验证失败, path={}, error={}", path, e.getMessage());
            return unauthorized(exchange, "Token无效或已过期");
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"code\":401,\"message\":\"" + message + "\",\"data\":null}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
