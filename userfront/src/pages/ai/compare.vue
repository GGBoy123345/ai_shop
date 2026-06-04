<template>
  <div class="ai-compare-page">
    <!-- Hero -->
    <section class="ai-hero">
      <div class="ai-hero-inner container">
        <span class="ai-hero-badge">AI 驱动</span>
        <h1 class="ai-hero-title">智能商品对比</h1>
        <p class="ai-hero-desc">选择多款商品，AI自动分析参数差异，给出专业推荐建议和详细理由，让选择不再困难</p>
      </div>
    </section>

    <!-- How it works -->
    <section class="how-section container">
      <h2 class="section-title section-accent">工作原理</h2>
      <div class="steps-grid">
        <div class="step-card">
          <div class="step-number">1</div>
          <h3 class="step-title">选择商品</h3>
          <p class="step-desc">从商品库中选择2-5款想要对比的商品</p>
        </div>
        <div class="step-arrow">→</div>
        <div class="step-card">
          <div class="step-number">2</div>
          <h3 class="step-title">AI 分析</h3>
          <p class="step-desc">AI自动解析商品参数，多维度对比分析</p>
        </div>
        <div class="step-arrow">→</div>
        <div class="step-card">
          <div class="step-number">3</div>
          <h3 class="step-title">获取推荐</h3>
          <p class="step-desc">获得AI推荐结论和详细的选择理由</p>
        </div>
      </div>
    </section>

    <!-- Demo Preview -->
    <section class="demo-section container">
      <h2 class="section-title section-accent">对比效果预览</h2>
      <div class="demo-panel">
        <div class="demo-products">
          <div class="demo-product">
            <div class="demo-img"></div>
            <div class="demo-name">商品 A</div>
            <div class="demo-price">¥2,999</div>
          </div>
          <div class="demo-vs">VS</div>
          <div class="demo-product">
            <div class="demo-img"></div>
            <div class="demo-name">商品 B</div>
            <div class="demo-price">¥3,499</div>
          </div>
        </div>

        <div class="demo-comparison">
          <div class="comp-row">
            <span class="comp-label">性能评分</span>
            <div class="comp-bars">
              <div class="comp-bar"><div class="comp-fill" style="width: 85%"></div></div>
              <div class="comp-bar"><div class="comp-fill" style="width: 92%"></div></div>
            </div>
          </div>
          <div class="comp-row">
            <span class="comp-label">性价比</span>
            <div class="comp-bars">
              <div class="comp-bar"><div class="comp-fill" style="width: 90%"></div></div>
              <div class="comp-bar"><div class="comp-fill" style="width: 78%"></div></div>
            </div>
          </div>
          <div class="comp-row">
            <span class="comp-label">用户评价</span>
            <div class="comp-bars">
              <div class="comp-bar"><div class="comp-fill" style="width: 76%"></div></div>
              <div class="comp-bar"><div class="comp-fill" style="width: 88%"></div></div>
            </div>
          </div>
        </div>

        <div class="demo-reasoning">
          <div class="reasoning-header">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20z"/><path d="M12 16v-4"/><path d="M12 8h.01"/></svg>
            <span>AI 推荐理由</span>
          </div>
          <p class="reasoning-text">
            综合分析两款商品的参数和评价，<strong>推荐商品 A</strong>。虽然商品 B 在性能评分上略高，但商品 A 的性价比优势明显（高出12%），且用户评价更稳定。对于日常使用场景，商品 A 提供了更好的综合体验。
          </p>
        </div>
      </div>
    </section>

    <!-- CTA -->
    <section class="cta-section container">
      <div class="cta-card">
        <h2 class="cta-title">即将上线</h2>
        <p class="cta-desc">AI 智能对比功能正在开发中，敬请期待</p>
        <div class="cta-actions">
          <button class="btn-primary" @click="$router.push('/category')">先去逛逛</button>
          <button class="btn-ghost" @click="$router.push('/')">返回首页</button>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, nextTick } from 'vue'
import { gsap } from 'gsap'

onMounted(() => {
  nextTick(() => {
    const tl = gsap.timeline({ defaults: { ease: 'power3.out' } })

    tl.from('.ai-hero-badge', { y: -20, opacity: 0, duration: 0.5 })
    tl.from('.ai-hero-title', { y: 40, opacity: 0, duration: 0.7 }, '-=0.2')
    tl.from('.ai-hero-desc', { y: 30, opacity: 0, duration: 0.5 }, '-=0.3')

    tl.from('.step-card', { y: 40, opacity: 0, duration: 0.5, stagger: 0.15 }, '-=0.2')
    tl.from('.step-arrow', { opacity: 0, duration: 0.3, stagger: 0.1 }, '-=0.4')

    tl.from('.demo-panel', { y: 50, opacity: 0, duration: 0.7 }, '-=0.2')
    tl.from('.comp-fill', { width: 0, duration: 0.8, stagger: 0.1, ease: 'power2.out' }, '-=0.3')
    tl.from('.demo-reasoning', { y: 20, opacity: 0, duration: 0.5 }, '-=0.3')

    tl.from('.cta-card', { scale: 0.95, opacity: 0, duration: 0.5 }, '-=0.2')
  })
})
</script>

<style scoped>
.ai-compare-page {
  padding-bottom: var(--space-4xl);
}

/* Hero */
.ai-hero {
  background: linear-gradient(135deg, #0f0c29, #302b63, #24243e);
  padding: var(--space-3xl) 0;
  text-align: center;
}
.ai-hero-badge {
  display: inline-block;
  padding: 4px 14px;
  background: rgba(102,126,234,0.2);
  border: 1px solid rgba(102,126,234,0.3);
  border-radius: var(--radius-full);
  font-size: 13px;
  font-weight: 500;
  color: #a78bfa;
  margin-bottom: var(--space-md);
}
.ai-hero-title {
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: 700;
  color: #fff;
  margin-bottom: var(--space-md);
}
.ai-hero-desc {
  font-size: 17px;
  color: rgba(255,255,255,0.6);
  max-width: 560px;
  margin: 0 auto;
  line-height: 1.6;
}

/* How it works */
.how-section {
  padding-top: var(--space-3xl);
}
.steps-grid {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  margin-top: var(--space-xl);
}
.step-card {
  flex: 1;
  text-align: center;
  padding: var(--space-xl);
  background: var(--surface);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
}
.step-number {
  width: 40px;
  height: 40px;
  margin: 0 auto var(--space-md);
  background: var(--brand-gradient);
  color: #fff;
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 700;
}
.step-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--space-sm);
}
.step-desc {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.5;
}
.step-arrow {
  font-size: 20px;
  color: var(--text-muted);
  flex-shrink: 0;
}

/* Demo */
.demo-section {
  padding-top: var(--space-3xl);
}
.demo-panel {
  margin-top: var(--space-xl);
  background: var(--surface);
  border-radius: var(--radius-lg);
  padding: var(--space-xl);
  box-shadow: var(--shadow-card);
}
.demo-products {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-xl);
  margin-bottom: var(--space-xl);
}
.demo-product {
  text-align: center;
}
.demo-img {
  width: 120px;
  height: 120px;
  background: linear-gradient(135deg, #f0f0f0, #e8e8e8);
  border-radius: var(--radius-lg);
  margin: 0 auto var(--space-sm);
}
.demo-name {
  font-size: 15px;
  font-weight: 500;
  color: var(--text-primary);
}
.demo-price {
  font-size: 16px;
  font-weight: 700;
  color: var(--price-red);
  margin-top: 4px;
}
.demo-vs {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-muted);
  padding: 8px 16px;
  background: #f5f5f5;
  border-radius: var(--radius-full);
}

.demo-comparison {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
  margin-bottom: var(--space-xl);
  padding: var(--space-lg);
  background: #f8f8f8;
  border-radius: var(--radius-lg);
}
.comp-row {
  display: flex;
  align-items: center;
  gap: var(--space-md);
}
.comp-label {
  width: 80px;
  font-size: 13px;
  color: var(--text-secondary);
  flex-shrink: 0;
}
.comp-bars {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.comp-bar {
  height: 8px;
  background: #e8e8e8;
  border-radius: var(--radius-full);
  overflow: hidden;
}
.comp-fill {
  height: 100%;
  background: var(--brand-gradient);
  border-radius: var(--radius-full);
  transition: width 0.8s ease;
}

.demo-reasoning {
  padding: var(--space-lg);
  background: rgba(102,126,234,0.04);
  border: 1px solid rgba(102,126,234,0.12);
  border-radius: var(--radius-lg);
}
.reasoning-header {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  font-size: 15px;
  font-weight: 600;
  color: var(--brand-start);
  margin-bottom: var(--space-md);
}
.reasoning-text {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.7;
}
.reasoning-text strong {
  color: var(--text-primary);
}

/* CTA */
.cta-section {
  padding-top: var(--space-3xl);
}
.cta-card {
  text-align: center;
  padding: var(--space-3xl);
  background: linear-gradient(135deg, rgba(102,126,234,0.06), rgba(118,75,162,0.06));
  border: 1px solid rgba(102,126,234,0.12);
  border-radius: var(--radius-lg);
}
.cta-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: var(--space-sm);
}
.cta-desc {
  font-size: 15px;
  color: var(--text-secondary);
  margin-bottom: var(--space-xl);
}
.cta-actions {
  display: flex;
  gap: var(--space-md);
  justify-content: center;
}
.btn-primary {
  padding: 12px 28px;
  background: var(--brand-gradient);
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  border-radius: var(--radius-full);
  box-shadow: 0 4px 15px rgba(102,126,234,0.35);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102,126,234,0.45);
}
.btn-ghost {
  padding: 12px 28px;
  background: transparent;
  color: var(--text-secondary);
  font-size: 15px;
  font-weight: 500;
  border: 1px solid #ddd;
  border-radius: var(--radius-full);
  transition: all 0.2s ease;
}
.btn-ghost:hover {
  border-color: var(--brand-start);
  color: var(--brand-start);
}

/* Responsive */
@media (max-width: 768px) {
  .steps-grid {
    flex-direction: column;
  }
  .step-arrow {
    transform: rotate(90deg);
  }
  .demo-products {
    flex-direction: column;
    gap: var(--space-md);
  }
  .cta-actions {
    flex-direction: column;
    align-items: center;
  }
}
</style>
