# Product

## Register

hybrid

Admin dashboard is a product surface (usability-first, efficient). User storefront is a brand surface (editorial, premium, personality-driven).

## Users

**Shoppers** (userfront): Web visitors browsing and buying products. They expect a premium, trustworthy experience — not a discount-bin marketplace. They interact with AI features (comparison, virtual try-on) to make confident purchase decisions.

**Merchants / Admins** (adminfront): Platform operators managing products, orders, users, and content. They need speed, clarity, and density — no visual noise.

## Product Purpose

AI商城 is an intelligent e-commerce platform built as a graduation project. It sells general merchandise with two differentiating AI capabilities:

1. **AI Smart Comparison**: Users select products, and the AI analyzes parameters across them, delivering a reasoned recommendation with explanation.
2. **AI Virtual Fitting Room**: Users provide body parameters; the system generates a visualization of how clothing items would look when worn.

These AI features are the product's core value proposition — the storefront should make them feel premium and trustworthy, not gimmicky.

## Brand Personality

**Premium, refined, editorial.** Inspired by Apple's product pages — spacious layouts, confident typography, restrained color, high-quality imagery that lets products breathe. Differentiated through: purposeful GSAP motion design, warmth in the AI interaction, and a focus on intelligent shopping rather than static product catalogs.

Three words: **Confident, Intelligent, Refined.**

## Anti-references

- **Taobao / Pinduoduo style**: Cluttered, discount-driven, overwhelming grids, flashing banners, "limited time" urgency tactics. The opposite of this project's personality.
- **Generic Shopify templates**: Cookie-cutter card grids with no editorial ambition. Looks like every other dropshipping store.
- **Dark-mode-for-cool SaaS dashboards**: The admin panel should stay light, professional, and Element Plus-native — not force a dark theme because "tools look cool dark."

## Design Principles

1. **Show the intelligence.** AI features are the product's backbone — they should feel integrated and premium, not bolted on. Every AI surface earns trust through clarity of reasoning and quality of presentation.
2. **Editorial restraint.** One strong visual moment per screen beats five competing elements. Let product imagery and typography carry the page. White space is structural, not wasted.
3. **Purposeful motion.** GSAP animations already exist in the codebase — use motion to guide attention, confirm interactions, and create a sense of craftsmanship. Every animation has a job; decoration-only motion gets cut.
4. **Desktop-first storefront.** The userfront is a web experience, not a mobile port. Layouts should use the full canvas — editorial widths, multi-column where it serves the content, rich hover states.
5. **Admin efficiency.** The admin dashboard prioritizes information density and task completion. No decorative elements that slow down merchants managing hundreds of products.

## Accessibility & Inclusion

- WCAG 2.1 AA minimum for both surfaces
- All GSAP animations must respect `prefers-reduced-motion: reduce`
- Price text and critical information must meet 4.5:1 contrast ratio
- Keyboard navigation for all interactive elements
- Chinese-language primary; design should accommodate CJK typography (line-height, character density)
