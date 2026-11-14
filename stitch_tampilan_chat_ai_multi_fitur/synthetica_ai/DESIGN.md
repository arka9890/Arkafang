---
name: Synthetica AI
colors:
  surface: '#0b1326'
  surface-dim: '#0b1326'
  surface-bright: '#31394d'
  surface-container-lowest: '#060e20'
  surface-container-low: '#131b2e'
  surface-container: '#171f33'
  surface-container-high: '#222a3d'
  surface-container-highest: '#2d3449'
  on-surface: '#dae2fd'
  on-surface-variant: '#c7c4d7'
  inverse-surface: '#dae2fd'
  inverse-on-surface: '#283044'
  outline: '#908fa0'
  outline-variant: '#464554'
  surface-tint: '#c0c1ff'
  primary: '#c0c1ff'
  on-primary: '#1000a9'
  primary-container: '#8083ff'
  on-primary-container: '#0d0096'
  inverse-primary: '#494bd6'
  secondary: '#d0bcff'
  on-secondary: '#3c0091'
  secondary-container: '#571bc1'
  on-secondary-container: '#c4abff'
  tertiary: '#7bd0ff'
  on-tertiary: '#00354a'
  tertiary-container: '#009bd1'
  on-tertiary-container: '#002d40'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#e1e0ff'
  primary-fixed-dim: '#c0c1ff'
  on-primary-fixed: '#07006c'
  on-primary-fixed-variant: '#2f2ebe'
  secondary-fixed: '#e9ddff'
  secondary-fixed-dim: '#d0bcff'
  on-secondary-fixed: '#23005c'
  on-secondary-fixed-variant: '#5516be'
  tertiary-fixed: '#c4e7ff'
  tertiary-fixed-dim: '#7bd0ff'
  on-tertiary-fixed: '#001e2c'
  on-tertiary-fixed-variant: '#004c69'
  background: '#0b1326'
  on-background: '#dae2fd'
  surface-variant: '#2d3449'
typography:
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 30px
    fontWeight: '700'
    lineHeight: 38px
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 22px
    fontWeight: '600'
    lineHeight: 28px
  headline-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 21px
  body-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 18px
  label-md:
    fontFamily: JetBrains Mono
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
  label-sm:
    fontFamily: JetBrains Mono
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 14px
  headline-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 32px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  margin: 1rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 0.75rem
  space-lg: 1.25rem
  space-xl: 2rem
---

## Brand & Style

The design system establishes a focused, intelligent, and fluid atmosphere tailored for modern conversational AI experiences on mobile. It pairs the calm restraint of functional minimalism with delicate, luminous digital cues—evoking a sense of instant synthesis, clarity, and computational depth without overwhelming cognitive bandwidth.

The audience consists of technical professionals, knowledge workers, creators, and power users who expect fast response generation, pristine legibility, and distraction-free interactions.

The visual direction centers on **Minimalism with Luminous Accents**:
- Deep, low-fatigue slate backgrounds anchored by subtle atmospheric luminescence.
- Translucent, soft-edged container structures that keep conversational threads front and center.
- Controlled spectral highlights—violet and soft indigo—applied strictly to AI telemetry, model status, and synthetic responses to immediately delineate human prompts from generated content.

## Colors

The palette revolves around deep nocturnal foundations punctuated by optical violet and indigo energy lines:

- **Primary (`#6366F1` - Electric Indigo):** Signals active intelligence, user prompt triggers, active navigation items, and prime interactive focus.
- **Secondary (`#8B5CF6` - Soft Violet):** Reserved for generative AI system tokens, streaming state indicators, model badges, and accent glow gradients.
- **Tertiary (`#38BDF8` - Cyan Glint):** Pinpoint semantic accents for code blocks, syntax tokening, copy confirmations, and network ping pings.
- **Neutral (`#0F172A` - Midnight Slate):** Base dark canvas. Supported by a graded scale of surfaces:
  - Surface Root: `#090D16`
  - Surface Ground: `#0F172A`
  - Surface Raised (Human Bubbles / Cards): `#1E293B`
  - Surface Overlay (Drawer / Sheets): `#131C31`
  - Surface Border: `rgba(255, 255, 255, 0.08)`
  - Text Primary: `#F8FAFC`
  - Text Secondary: `#94A3B8`
  - Text Muted: `#64748B`

In light mode (system fallback), the canvas flips to a pure white and chalk foundation (`#F8FAFC`, `#FFFFFF`), neutralizing AI backdrops to crisp porcelain while retaining the indigo-violet signatures for state highlights and icons.

## Typography

Typography prioritizes rapid conversational scanning, distinct semantic rendering, and high-legibility rendering during long reading sessions:

- **Display & Headings (Plus Jakarta Sans):** Introduces friendly precision and balanced geometry for screen titles, thread headers, provider modal titles, and empty-state orientations.
- **Body & Continuous Flow (Inter):** Serves as the primary vehicle for both user submissions and generated AI prose. High x-height, neutral letterforms, and optimized terminal spacing maintain effortless scanning across complex paragraphs, lists, and formatting.
- **Labels & Code (JetBrains Mono):** Drives telemetry, token counters, model identifier chips (e.g., `gpt-4o`, `claude-3-5-sonnet`), timestamps, and raw code output blocks.

## Layout & Spacing

The layout is built for continuous mobile touch ergonomics:

- **Safe-Zone Framework:** All viewports reserve a top app bar zone (56px) for the hamburger navigation trigger, provider badge, and thread action menu, alongside a dedicated anchored bottom input dock (64px + safe area padding).
- **Conversational Vertical Rhythm:** Chat bubbles and elements operate on a vertical stack separated by `space-sm` (same-speaker sequential) and `space-md` (turn-taking transition). 
- **The Drawer Paradigm:** The left-hand drawer occupies 82% of screen width (max 340px) to safeguard one-handed thumb dismissal via back-swipe, overlaying the active thread with a deep backdrop dim.
- **Bottom Navigation Dock:** Floating or docked at bottom with symmetrical icon spacing, providing seamless access to primary destinations (Chat, History, Explore, Prompts, Settings).

## Elevation & Depth

Visual hierarchy leverages a hybrid of **Tonal Layering** and **Luminous Glassmorphism**:

- **Layer 0 (Canvas):** Pure `#090D16` root screen level.
- **Layer 1 (Card & Bubbles):** `#1E293B` with a hair-thin perimeter outline (`rgba(255, 255, 255, 0.06)`).
- **Layer 2 (Overlays & Drawer):** `#131C31` with `backdrop-filter: blur(20px)` and an ambient shadow: `0 16px 36px -8px rgba(0, 0, 0, 0.65)`.
- **Layer 3 (Floating Action Docks & Tooltips):** Semi-opaque surfaces (`rgba(19, 28, 49, 0.85)`) framed by a subtle top specular border (`rgba(99, 102, 241, 0.25)`) and diffused indigo back-glow (`0 0 24px -4px rgba(99, 102, 241, 0.15)`).

No heavy dropshadows or muddy blurs are permitted on informational content; elevation is achieved through surface luminance adjustments and fine edge boundaries.

## Shapes

The interface balances organic human conversation with digital precision through moderate, consistent curves:

- **Base Components (Inputs, System Cards, Dropdowns):** Defined at `roundedness: 2` (8px / `0.5rem`).
- **Containers & Drawers:** Feature soft `rounded-lg` (16px / `1rem`) corner radiuses on leading surface corners.
- **Message Bubbles:** Asymmetric rounding—human bubbles feature `rounded-lg` with a pinched bottom-right anchor (4px); AI responses utilize a cleaner open-flow layout or soft `rounded-lg` on all corners to communicate ambient generation.
- **Action Pills & Badges:** Use full circular/capsule bounds for quick tactile recognition.

## Components

### Message Bubbles
- **User Bubble:** Aligned right, background `#1E293B`, subtle highlight border (`rgba(255, 255, 255, 0.08)`), text `#F8FAFC`.
- **AI Bubble:** Aligned left, transparent to ultra-soft slate (`rgba(30, 41, 59, 0.4)`), zero heavy borders. Framed by an AI identity avatar or soft gradient spark icon in `#8B5CF6`. Content includes formatted Markdown, seamless inline code wrappers, and micro-actions (copy, retry, branch, rate) positioned beneath the response.

### Chat Input Dock
- Integrated bar pinned to the bottom keyboard guide.
- Features a multi-line auto-expanding input field, attachment icon, voice dictation trigger, and an dynamic send button that shifts from muted gray to an Indigo/Violet radial gradient on input detection.

### Hamburger Menu Drawer (Sidebar)
- Slides smoothly from left-edge touch trigger or tap.
- Contains:
  1. **Provider Selector:** Segmented control or dropdown (e.g., Anthropic Claude, OpenAI GPT, Local LLM) with latency and context-window indicators.
  2. **Thread Groups:** Grouped chronologically (Today, Previous 7 Days, Archive) with swipe-to-delete and pin triggers.
  3. **Workspace/Account & Settings:** Anchored at the bottom with profile avatar, theme switch toggle, and token quota telemetry.

### Bottom Navigation Bar
- High-blur frosted bar (`rgba(15, 23, 42, 0.85)`).
- Contains 4 to 5 core navigation points with 24px clean-line vector icons. Active states illuminate with an indigo glow dot beneath the icon and an icon color transition to `#6366F1`.

### Chips & Provider Badges
- Pill-shaped chips (`rounded-full`) utilizing `JetBrains Mono` for model versions (`label-sm`).
- Bordered in `rgba(99, 102, 241, 0.3)` with soft background fill `rgba(99, 102, 241, 0.08)`.

### Code & Artifact Blocks
- Jet-black inset containers (`#070A11`) embedded within AI responses.
- Header row includes language indicator, execution timer, and a one-tap copy button with instant checkmark feedback.