---
name: AI商城
description: Premium intelligent e-commerce platform — editorial restraint meets AI-powered shopping
colors:
  brand-gradient-start: "#667eea"
  brand-gradient-end: "#764ba2"
  price-red: "#ee0a24"
  page-bg: "#f5f5f5"
  surface: "#ffffff"
  text-primary: "#333333"
  text-secondary: "#666666"
  text-muted: "#999999"
  link-blue: "#1989fa"
  admin-primary: "#409EFF"
  admin-sidebar: "#304156"
  admin-sidebar-text: "#bfcbd9"
  admin-content-bg: "#f0f2f5"
  tag-hot: "#ee0a24"
  tag-new: "#07c160"
  tag-recommend: "#ff976a"
  success: "#67C23A"
  warning: "#E6A23C"
  danger: "#F56C6C"
  shadow-light: "rgba(0,0,0,0.06)"
  shadow-medium: "rgba(0,0,0,0.12)"
typography:
  display:
    fontFamily: "system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'Noto Sans SC', 'PingFang SC', 'Microsoft YaHei', sans-serif"
    fontSize: "clamp(2rem, 5vw, 3.5rem)"
    fontWeight: 700
    lineHeight: 1.15
    letterSpacing: "-0.02em"
  headline:
    fontFamily: "system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'Noto Sans SC', 'PingFang SC', 'Microsoft YaHei', sans-serif"
    fontSize: "1.5rem"
    fontWeight: 600
    lineHeight: 1.3
  title:
    fontFamily: "system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'Noto Sans SC', 'PingFang SC', 'Microsoft YaHei', sans-serif"
    fontSize: "1.125rem"
    fontWeight: 500
    lineHeight: 1.4
  body:
    fontFamily: "system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'Noto Sans SC', 'PingFang SC', 'Microsoft YaHei', sans-serif"
    fontSize: "0.875rem"
    fontWeight: 400
    lineHeight: 1.6
  label:
    fontFamily: "system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'Noto Sans SC', 'PingFang SC', 'Microsoft YaHei', sans-serif"
    fontSize: "0.75rem"
    fontWeight: 500
    lineHeight: 1.4
    letterSpacing: "0.02em"
rounded:
  sm: "4px"
  md: "8px"
  lg: "12px"
  full: "9999px"
spacing:
  xs: "4px"
  sm: "8px"
  md: "16px"
  lg: "24px"
  xl: "32px"
  xxl: "48px"
components:
  button-primary:
    backgroundColor: "linear-gradient(135deg, {colors.brand-gradient-start}, {colors.brand-gradient-end})"
    textColor: "#ffffff"
    rounded: "{rounded.full}"
    padding: "12px 32px"
    height: "48px"
  button-primary-hover:
    backgroundColor: "linear-gradient(135deg, {colors.brand-gradient-start}, {colors.brand-gradient-end})"
    textColor: "#ffffff"
  card-product:
    backgroundColor: "{colors.surface}"
    rounded: "{rounded.lg}"
    padding: "0"
  card-product-hover:
    backgroundColor: "{colors.surface}"
  input-search:
    backgroundColor: "{colors.surface}"
    rounded: "{rounded.full}"
    padding: "8px 16px"
  nav-tabbar:
    backgroundColor: "{colors.surface}"
    textColor: "{colors.text-muted}"
  nav-tabbar-active:
    backgroundColor: "{colors.surface}"
    textColor: "{colors.brand-gradient-start}"
  admin-sidebar:
    backgroundColor: "{colors.admin-sidebar}"
    textColor: "{colors.admin-sidebar-text}"
  admin-sidebar-active:
    backgroundColor: "{colors.admin-sidebar}"
    textColor: "{colors.admin-primary}"
---

# Design System: AI商城

## 1. Overview

**Creative North Star: "The Atelier"**

AI商城 is a premium e-commerce platform that treats product presentation as editorial craft. Inspired by Apple's product pages — spacious, confident, image-forward — but differentiated through intelligent AI features and purposeful motion. The design system rejects the cluttered, discount-driven visual language of typical Chinese e-commerce (Taobao, Pinduoduo) in favor of restraint that lets products breathe and AI features earn trust.

The userfront is a desktop-first web experience, not a mobile port. Layouts use the full browser canvas: editorial widths, generous whitespace, multi-column product grids, rich hover states. The admin dashboard is a separate register — efficient, dense, Element Plus-native — where merchants manage inventory without decorative friction.

**Key Characteristics:**
- Editorial restraint: one strong visual moment per screen, white space as structure
- Brand gradient as accent voice, not wallpaper — used sparingly on CTAs, section accents, and interactive highlights
- Desktop-first storefront with full-canvas layouts and purposeful GSAP motion
- AI features (comparison, virtual try-on) presented with clarity and trustworthiness
- Clean separation between brand surface (userfront) and product surface (adminfront)

## 2. Colors: The Indigo-Violet Palette

The palette is built around a single brand gradient — indigo (#667eea) to violet (#764ba2) — that carries the platform's identity. It appears on primary CTAs, section accent bars, login surfaces, and interactive highlights. The gradient is the brand's voice; its rarity on any given screen is the point.

### Primary

- **Brand Indigo** (#667eea): The warm anchor of the brand gradient. Used on primary buttons, active tab indicators, section accent bars (the 4px `::before` pattern), and link underlines. Appears as the starting point of the signature 135deg gradient.
- **Brand Violet** (#764ba2): The cool complement. Always paired with Brand Indigo in the gradient; never used alone. Together they form the brand gradient: `linear-gradient(135deg, #667eea, #764ba2)`.

### Secondary

- **Price Red** (#ee0a24): The commerce accent. Reserved exclusively for prices, price highlights, and "hot" product tags. Never used for general UI chrome. Its scarcity signals urgency and draws the eye to purchase decisions.
- **Link Blue** (#1989fa): Interactive accent for category navigation active states and SKU selection borders. Functional, not decorative.

### Tertiary

- **Tag Green** (#07c160): "New" product badges and success states.
- **Tag Orange** (#ff976a): "Recommend" product badges and warning states.
- **Admin Blue** (#409EFF): Element Plus primary, used only in the admin dashboard for menu active states, stat card accents, and chart lines.

### Neutral

- **Page Background** (#f5f5f5): The quiet canvas. Both surfaces use a light neutral ground that recedes behind white content surfaces.
- **Surface White** (#ffffff): Cards, cells, panels, input fields. The primary content surface.
- **Text Primary** (#333333): Body text, headings, titles. High contrast against white; meets WCAG AA.
- **Text Secondary** (#666666): Meta information, descriptions, supporting copy.
- **Text Muted** (#999999): Placeholders, timestamps, tertiary labels.
- **Admin Sidebar** (#304156): The dark container for the admin navigation. A distinct visual world from the content area.
- **Admin Sidebar Text** (#bfcbd9): Muted light text on the dark sidebar; white on active items.

### Named Rules

**The Gradient Discipline Rule.** The brand gradient appears on primary CTAs, section accent bars, login surfaces, and decorative circles. It is never used as a page background, card fill, or text color. Its presence should feel intentional, not ambient.

**The Price Red Rule.** #ee0a24 is reserved for prices and "hot" tags. Using it for errors, warnings, or general emphasis dilutes its commercial signal.

## 3. Typography

**Display Font:** system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'Noto Sans SC', 'PingFang SC', 'Microsoft YaHei', sans-serif
**Body Font:** Same stack
**Label Font:** Same stack

**Character:** A single system font stack with committed weight and size contrast. The system stack prioritizes CJK rendering quality (Noto Sans SC, PingFang SC, Microsoft YaHei) while using the platform's native Latin sans for English text. The personality comes from scale and weight contrast, not from typeface choice — large, bold display headings (700 weight) against light body text (400 weight) at 14px.

### Hierarchy

- **Display** (700, clamp(2rem, 5vw, 3.5rem), line-height 1.15, letter-spacing -0.02em): Hero headings on the login page, major section titles. Maximum display size is 3.5rem (56px); above that reads as shouting, not designing.
- **Headline** (600, 24px, line-height 1.3): Section titles ("推荐商品", "商品详情"), card group headings.
- **Title** (500, 18px, line-height 1.4): Product titles, card headings, navigation labels.
- **Body** (400, 14px, line-height 1.6): Default text. Max line length 65–75ch for prose sections.
- **Label** (500, 12px, line-height 1.4, letter-spacing 0.02em): Product tags, meta labels, timestamps, tab labels.

### Named Rules

**The Weight Contrast Rule.** Hierarchy is built through weight contrast (700 → 600 → 500 → 400) and size jumps (≥1.25× ratio between levels), not through font-family switching. A single family with committed weight contrast reads as confident; two similar families read as indecision.

**The CJK Line Height Rule.** Chinese text requires 0.1–0.15 more line-height than Latin text at the same size. Body text uses 1.6; headings use 1.15–1.4. Tighter line-height on CJK body text makes paragraphs feel cramped and unreadable.

## 4. Elevation

The system uses a restrained shadow vocabulary. Surfaces are flat at rest; shadows appear only as responses to state (hover, lift, focus). The approach is structural, not ambient — shadows communicate interactivity and depth hierarchy, not decoration.

### Shadow Vocabulary

- **Card Rest** (`box-shadow: 0 2px 8px rgba(0,0,0,0.06)`): Default state for product cards, cell groups, and content panels. Barely visible; just enough to separate the surface from the page background.
- **Card Hover** (`box-shadow: 0 8px 24px rgba(0,0,0,0.12)`): Elevated state on hover, paired with `translateY(-4px)` lift. Communicates interactivity.
- **Dropdown** (`box-shadow: 0 8px 24px rgba(0,0,0,0.12)`): Search suggestion dropdowns, popover menus. Same density as card hover but without the lift transform.
- **Header** (`box-shadow: 0 1px 4px rgba(0,0,0,0.1)`): Subtle separation for sticky headers and admin header bar.

### Named Rules

**The Flat-By-Default Rule.** Every surface starts flat (no shadow). Shadows are earned through interaction — hover, elevation, or focus. A card that ships with a shadow at rest has already spent its visual budget.

**The Shadow-Transform Pairing Rule.** A hover shadow is always paired with a `translateY` lift (2–4px). Shadow without transform looks like a rendering artifact, not an intentional elevation change.

## 5. Components

### Buttons

- **Shape:** Fully rounded pill (`border-radius: 9999px`). The round shape is the brand's button identity.
- **Primary:** White text on the brand gradient (`linear-gradient(135deg, #667eea, #764ba2)`). Height 48px, font-size 16px, font-weight 600, padding 12px 32px. Box-shadow: `0 4px 15px rgba(102,126,234,0.35)`.
- **Hover:** `translateY(-3px) scale(1.02)`, shadow intensifies. Transition: 0.3s ease.
- **Active/Press:** `scale(0.98)`, shadow reduces. Immediate tactile feedback.
- **Secondary / Ghost:** Transparent background, brand gradient text (via `background-clip: text`), gradient border via pseudo-element. Used for secondary CTAs that need to feel lighter.
- **Admin buttons:** Standard Element Plus defaults. No gradient, no pill shape. The admin surface stays native to its framework.

### Product Cards

- **Corner Style:** Gently curved (12px radius). Consistent across all userfront cards.
- **Background:** Surface white (#ffffff).
- **Shadow Strategy:** Card Rest at rest, Card Hover on hover with translateY(-4px) lift.
- **Border:** None. Shadow-only separation.
- **Internal Padding:** Image fills the card top; text area has 12px padding.
- **Content Pattern:** Product image (fixed height 240px, `object-fit: cover`), product title (2-line clamp, 17px/500), price (18–28px bold, #ee0a24). Optional product tag badge (hot/new/recommend) positioned absolute on the image.
- **Grid:** 2-column grid with gap. Desktop-first: `repeat(auto-fill, minmax(280px, 1fr))`.

### Product Tags

- **Style:** Small colored badges. Font-size 10px, padding 1px 4px, border-radius 3px, white text.
- **Variants:** Hot (#ee0a24), New (#07c160), Recommend (#ff976a). Each is a distinct semantic role, not interchangeable.

### Search Bar

- **Style:** Vant `van-search` with `shape="round"`, sticky at top (`position: sticky; top: 0; z-index: 10`).
- **Suggestions Dropdown:** White background, 12px radius, Card Hover shadow. GSAP slide-in animation from below.
- **Focus State:** Subtle blue tint on the input field background.

### Section Title Accent Bar

- **Pattern:** `::before` pseudo-element — 4px wide, 18–20px tall, brand gradient fill, 2px radius. Positioned to the left of the section heading.
- **Usage:** One per major content section. Not on every heading — reserved for section-level titles that introduce a new content area.

### Navigation (Userfront)

- **Tab Bar:** Vant bottom tabbar, 50px height. 4 tabs: Home, Category, Cart, User.
- **Active State:** Brand Indigo (#667eea) icon and label. Default: Text Muted (#999999).
- **Cart Badge:** Vant badge showing item count on the cart tab icon.

### Navigation (Admin)

- **Sidebar:** Dark background (#304156), 200px width (collapses to 64px). Element Plus `el-menu` with 12 items.
- **Active State:** Admin Blue (#409EFF) text and left border indicator. Default: Sidebar Text (#bfcbd9).
- **Header:** White background, breadcrumb left, admin dropdown right. Header Shadow for separation.

### Cards / Containers (Admin)

- **Style:** Standard Element Plus `el-card` with `shadow="hover"` default.
- **Stat Cards:** Custom flex layout — 56×56px colored icon square (12px radius) with text. Each stat uses a semantic color (merchants: #67C23A, products: #E6A23C, orders: #F56C6C).

## 6. Do's and Don'ts

### Do:

- **Do** use the brand gradient exclusively on primary CTAs, section accent bars, login surfaces, and decorative circles. Its rarity is the point.
- **Do** pair every hover shadow with a `translateY` transform (2–4px). Shadow without lift looks accidental.
- **Do** keep product cards at 12px radius. This is the userfront's corner standard.
- **Do** use `#ee0a24` only for prices and "hot" tags. Its scarcity signals commerce.
- **Do** build type hierarchy through weight contrast (700 → 400) and size jumps (≥1.25× ratio), not font-family switching.
- **Do** respect `prefers-reduced-motion: reduce` — every GSAP animation needs a crossfade or instant alternative.
- **Do** keep the admin dashboard Element Plus-native. No gradient buttons, no pill shapes, no brand flourishes. The admin is a tool, not a showcase.
- **Do** use the full browser canvas for the userfront storefront. Desktop-first layouts, editorial widths, generous whitespace.

### Don't:

- **Don't** use the brand gradient as a page background, card fill, or text color. PRODUCT.md calls for "editorial restraint"; a gradient wallpaper is the opposite.
- **Don't** use #ee0a24 for errors, warnings, or general emphasis. It's the price signal; overuse dilutes it.
- **Don't** apply shadows to resting-state surfaces. "The Flat-By-Default Rule": shadows are earned through interaction.
- **Don't** use border-radius above 16px on cards. 24/28/32/40px reads as "insanely rounded," not premium.
- **Don't** pair `border: 1px solid X` with `box-shadow: 0 Npx Mpx` where M ≥ 16px on the same element. The "ghost-card" pattern — 1px border plus soft wide shadow — is a codex tell. Pick one.
- **Don't** add tiny uppercase tracked eyebrows above every section heading. One strong kicker is voice; an eyebrow on every section is AI grammar.
- **Don't** use gradient text (`background-clip: text` with a gradient). Decorative, never meaningful.
- **Don't** force dark mode on the admin dashboard. PRODUCT.md explicitly rejects "dark-mode-for-cool SaaS dashboards."
- **Don't** use the Vant mobile layout patterns for the userfront. The userfront is a desktop web experience; layouts should use the full canvas, not mobile-first column stacking.
- **Don't** use all-caps body copy. Reserve uppercase for short labels (≤4 words) and product tag badges.
- **Don't** let display headings exceed 3.5rem (56px). Above that the page is shouting, not designing.
