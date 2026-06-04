<template>
  <div class="ai-tryon-page">
    <!-- Hero -->
    <section class="ai-hero">
      <div class="ai-hero-inner container">
        <span class="ai-hero-badge">AI 驱动</span>
        <h1 class="ai-hero-title">线上试衣间</h1>
        <p class="ai-hero-desc">输入你的身材参数，AI生成穿着效果预览，买前先看上身效果，告别盲目下单</p>
      </div>
    </section>

    <!-- How it works -->
    <section class="how-section container">
      <h2 class="section-title section-accent">如何使用</h2>
      <div class="steps-grid">
        <div class="step-card">
          <div class="step-icon">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
          </div>
          <h3 class="step-title">填写身材参数</h3>
          <p class="step-desc">输入身高、体重、肩宽等基本身材数据</p>
        </div>
        <div class="step-card">
          <div class="step-icon">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M20.38 3.46L16 2 12 3.46 8 2 3.62 3.46a2 2 0 0 0-1.34 1.89v13.3a2 2 0 0 0 2.66 1.89L8 19l4-1.46L16 19l4.38-1.46a2 2 0 0 0 1.34-1.89V5.35a2 2 0 0 0-1.34-1.89z"/></svg>
          </div>
          <h3 class="step-title">选择商品</h3>
          <p class="step-desc">浏览商品库，选择想要试穿的服装</p>
        </div>
        <div class="step-card">
          <div class="step-icon">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="2" y="3" width="20" height="14" rx="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/></svg>
          </div>
          <h3 class="step-title">查看效果</h3>
          <p class="step-desc">AI生成穿着效果预览，直观感受上身效果</p>
        </div>
      </div>
    </section>

    <!-- Demo Preview -->
    <section class="demo-section container">
      <h2 class="section-title section-accent">效果预览</h2>
      <div class="demo-layout">
        <!-- Form Preview -->
        <div class="demo-form">
          <h3 class="demo-form-title">身材参数</h3>
          <div class="form-group">
            <label class="form-label">身高</label>
            <div class="form-input-demo">175 cm</div>
          </div>
          <div class="form-group">
            <label class="form-label">体重</label>
            <div class="form-input-demo">68 kg</div>
          </div>
          <div class="form-group">
            <label class="form-label">肩宽</label>
            <div class="form-input-demo">44 cm</div>
          </div>
          <div class="form-group">
            <label class="form-label">胸围</label>
            <div class="form-input-demo">96 cm</div>
          </div>
          <div class="form-group">
            <label class="form-label">腰围</label>
            <div class="form-input-demo">80 cm</div>
          </div>
          <div class="form-submit-demo">生成预览</div>
        </div>

        <!-- Result Preview -->
        <div class="demo-result">
          <div class="result-placeholder">
            <div class="result-silhouette">
              <svg width="120" height="200" viewBox="0 0 120 200" fill="none">
                <ellipse cx="60" cy="28" rx="20" ry="24" fill="rgba(102,126,234,0.15)" stroke="rgba(102,126,234,0.3)" stroke-width="1.5"/>
                <path d="M40 52 L30 120 L25 180 L45 180 L50 120 L60 110 L70 120 L75 180 L95 180 L90 120 L80 52 Z" fill="rgba(102,126,234,0.1)" stroke="rgba(102,126,234,0.25)" stroke-width="1.5"/>
                <path d="M40 52 L15 95 L25 100 L42 65" fill="rgba(102,126,234,0.08)" stroke="rgba(102,126,234,0.2)" stroke-width="1"/>
                <path d="M80 52 L105 95 L95 100 L78 65" fill="rgba(102,126,234,0.08)" stroke="rgba(102,126,234,0.2)" stroke-width="1"/>
              </svg>
            </div>
            <p class="result-text">AI 试衣效果预览区域</p>
            <span class="result-hint">选择商品后生成穿着效果</span>
          </div>
        </div>
      </div>
    </section>

    <!-- Features -->
    <section class="features-section container">
      <h2 class="section-title section-accent">核心优势</h2>
      <div class="features-grid">
        <div class="feature-card">
          <h3 class="feature-title">精准模拟</h3>
          <p class="feature-desc">基于真实身材数据生成穿着效果，尺码推荐更准确</p>
        </div>
        <div class="feature-card">
          <h3 class="feature-title">减少退换</h3>
          <p class="feature-desc">买前预览上身效果，有效降低因尺码不合导致的退换率</p>
        </div>
        <div class="feature-card">
          <h3 class="feature-title">多角度查看</h3>
          <p class="feature-desc">支持正面、侧面等多角度预览，全面了解穿着效果</p>
        </div>
      </div>
    </section>

    <!-- CTA -->
    <section class="cta-section container">
      <div class="cta-card">
        <h2 class="cta-title">即将上线</h2>
        <p class="cta-desc">AI 线上试衣间功能正在开发中，敬请期待</p>
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

    tl.from('.demo-form', { x: -40, opacity: 0, duration: 0.6 }, '-=0.2')
    tl.from('.demo-result', { x: 40, opacity: 0, duration: 0.6 }, '-=0.4')
    tl.from('.result-silhouette', { scale: 0.8, opacity: 0, duration: 0.6, ease: 'back.out(1.5)' }, '-=0.2')

    tl.from('.feature-card', { y: 30, opacity: 0, duration: 0.4, stagger: 0.1 }, '-=0.2')
    tl.from('.cta-card', { scale: 0.95, opacity: 0, duration: 0.5 }, '-=0.2')
  })
})
</script>

<style scoped>
.ai-tryon-page {
  padding-bottom: var(--space-4xl);
}

/* Hero */
.ai-hero {
  background: linear-gradient(135deg, #1a0a2e, #2d1b4e, #1a1a3e);
  padding: var(--space-3xl) 0;
  text-align: center;
}
.ai-hero-badge {
  display: inline-block;
  padding: 4px 14px;
  background: rgba(118,75,162,0.2);
  border: 1px solid rgba(118,75,162,0.3);
  border-radius: var(--radius-full);
  font-size: 13px;
  font-weight: 500;
  color: #c084fc;
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

/* Steps */
.how-section {
  padding-top: var(--space-3xl);
}
.steps-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-lg);
  margin-top: var(--space-xl);
}
.step-card {
  text-align: center;
  padding: var(--space-xl);
  background: var(--surface);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
}
.step-icon {
  width: 56px;
  height: 56px;
  margin: 0 auto var(--space-md);
  background: rgba(118,75,162,0.08);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #764ba2;
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

/* Demo */
.demo-section {
  padding-top: var(--space-3xl);
}
.demo-layout {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: var(--space-xl);
  margin-top: var(--space-xl);
}

/* Demo Form */
.demo-form {
  background: var(--surface);
  border-radius: var(--radius-lg);
  padding: var(--space-xl);
  box-shadow: var(--shadow-card);
}
.demo-form-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--space-lg);
}
.form-group {
  margin-bottom: var(--space-md);
}
.form-label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
  margin-bottom: 6px;
}
.form-input-demo {
  padding: 10px 14px;
  background: #f8f8f8;
  border: 1px solid #e8e8e8;
  border-radius: var(--radius-md);
  font-size: 14px;
  color: var(--text-primary);
}
.form-submit-demo {
  margin-top: var(--space-lg);
  padding: 12px;
  background: var(--brand-gradient);
  color: #fff;
  text-align: center;
  font-size: 14px;
  font-weight: 600;
  border-radius: var(--radius-md);
  cursor: default;
}

/* Demo Result */
.demo-result {
  background: var(--surface);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
  overflow: hidden;
}
.result-placeholder {
  height: 100%;
  min-height: 380px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--space-md);
  background: linear-gradient(135deg, rgba(102,126,234,0.03), rgba(118,75,162,0.03));
}
.result-silhouette {
  opacity: 0.8;
}
.result-text {
  font-size: 15px;
  font-weight: 500;
  color: var(--text-secondary);
}
.result-hint {
  font-size: 13px;
  color: var(--text-muted);
}

/* Features */
.features-section {
  padding-top: var(--space-3xl);
}
.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-lg);
  margin-top: var(--space-xl);
}
.feature-card {
  padding: var(--space-xl);
  background: var(--surface);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
  border-left: 3px solid transparent;
  transition: border-color 0.3s ease, transform 0.3s ease;
}
.feature-card:hover {
  border-left-color: #764ba2;
  transform: translateY(-2px);
}
.feature-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--space-sm);
}
.feature-desc {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.5;
}

/* CTA */
.cta-section {
  padding-top: var(--space-3xl);
}
.cta-card {
  text-align: center;
  padding: var(--space-3xl);
  background: linear-gradient(135deg, rgba(118,75,162,0.06), rgba(102,126,234,0.06));
  border: 1px solid rgba(118,75,162,0.12);
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
  border-color: #764ba2;
  color: #764ba2;
}

/* Responsive */
@media (max-width: 900px) {
  .demo-layout {
    grid-template-columns: 1fr;
  }
}
@media (max-width: 768px) {
  .steps-grid, .features-grid {
    grid-template-columns: 1fr;
  }
  .cta-actions {
    flex-direction: column;
    align-items: center;
  }
}
</style>
