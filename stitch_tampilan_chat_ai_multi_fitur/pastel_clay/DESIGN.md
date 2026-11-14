---
name: Pastel Clay
colors:
  surface: '#fbf8ff'
  surface-dim: '#d1d8ff'
  surface-bright: '#fbf8ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f3f2ff'
  surface-container: '#ecedff'
  surface-container-high: '#e4e7ff'
  surface-container-highest: '#dde1ff'
  on-surface: '#0e193e'
  on-surface-variant: '#444655'
  inverse-surface: '#242e55'
  inverse-on-surface: '#efefff'
  outline: '#747686'
  outline-variant: '#c4c5d7'
  surface-tint: '#2e4edc'
  primary: '#2346d5'
  on-primary: '#ffffff'
  primary-container: '#4361ee'
  on-primary-container: '#f4f2ff'
  inverse-primary: '#bac3ff'
  secondary: '#3f5aab'
  on-secondary: '#ffffff'
  secondary-container: '#8ea8ff'
  on-secondary-container: '#1a398a'
  tertiary: '#435878'
  on-tertiary: '#ffffff'
  tertiary-container: '#5c7091'
  on-tertiary-container: '#f0f3ff'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dee1ff'
  primary-fixed-dim: '#bac3ff'
  on-primary-fixed: '#001159'
  on-primary-fixed-variant: '#0031c4'
  secondary-fixed: '#dbe1ff'
  secondary-fixed-dim: '#b5c4ff'
  on-secondary-fixed: '#00174d'
  on-secondary-fixed-variant: '#244192'
  tertiary-fixed: '#d5e3ff'
  tertiary-fixed-dim: '#b3c7ed'
  on-tertiary-fixed: '#031c39'
  on-tertiary-fixed-variant: '#334767'
  background: '#fbf8ff'
  on-background: '#0e193e'
  surface-variant: '#dde1ff'
typography:
  headline-xl:
    fontFamily: Plus Jakarta Sans
    fontSize: 40px
    fontWeight: '800'
    lineHeight: 48px
    letterSpacing: -0.03em
  headline-xl-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 30px
    fontWeight: '800'
    lineHeight: 38px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 32px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
    letterSpacing: -0.015em
  headline-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
    letterSpacing: -0.01em
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.01em
  label-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.02em
  label-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 11px
    fontWeight: '700'
    lineHeight: 14px
    letterSpacing: 0.04em
rounded:
  sm: 0.5rem
  DEFAULT: 1rem
  md: 1.5rem
  lg: 2rem
  xl: 3rem
  full: 9999px
spacing:
  gutter: 1.5rem
  gutter-mobile: 1rem
  margin: 2.5rem
  margin-mobile: 1.25rem
  space-xs: 0.375rem
  space-sm: 0.75rem
  space-md: 1.25rem
  space-lg: 2rem
  space-xl: 3rem
---

## Brand & Style

This design system expresses a Minimalist Claymorphism aesthetic: tactile, welcoming, and modern without unnecessary ornamentation. It merges the physical warmth of sculpted clay with the strict clarity of modern digital minimalism. 

Targeted at modern productivity, wellness, and collaborative creative applications, the interface evokes a sense of calm reassurance, tactile satisfaction, and effortless control. Every card and interactive element feels like a smoothed, touchable pebble suspended within a serene atmosphere.

Key visual attributes:
- **Volumetric Pillow Forms:** Generous border radii, double-drop diffuse shadows, and delicate top-inset lighting create an extruded, matte 3D appearance without glossy skeuomorphism.
- **Airy Pastel Atmosphere:** Monochromatic, low-saturation blue tints maintain cognitive calm while letting focal elements stand out.
- **Sculptural Minimalism:** Generous internal whitespace, simplified iconography, and stark typography counteract the richness of the clay volumes, keeping the UI light and uncluttered.

## Colors

The palette revolves around a continuous spectrum of ice blues, pale periwinkles, and frosted whites, grounded by deep slate navy to guarantee effortless WCAG AAA text legibility.

- **Primary (`#4361EE`):** A vibrant periwinkle blue used purposefully for key interactive states, floating action buttons, focused controls, and critical highlights.
- **Secondary (`#8EA8FF`):** A softened cornflower tone for secondary badges, subtle active states, and pill toggles.
- **Tertiary (`#C4D9FF`):** A delicate sky tint serving as an intermediate container fill or subtle selection state.
- **Neutral (`#1B254B`):** A deep, saturated navy slate providing high-contrast readability across all light pastel and frosted surfaces.
- **Atmospheric Canvas & Cards:** Base canvas uses an ethereal ice blue (`#F0F4FC`), layered with frosted milk white (`#FFFFFF`) and pale periwinkle wash (`#E6EEFA`) to build sculptural depth.

## Typography

The typography leverages **Plus Jakarta Sans** across all levels. Its balanced geometric construction and clean, wide counters harmonize with rounded pill containers while maintaining immaculate clarity.

- **Headlines:** Display weights (700 and 800) use negative letter-spacing to form tight, cohesive headings that visually anchor the soft, buoyant clay panels.
- **Body:** Standardized at weight 400 with open line-heights to promote breathing room and comfortable scanning.
- **Labels:** Rendered in medium and semi-bold weights with slight positive tracking to ensure fast identification across tags, buttons, and tabular summaries.

## Layout & Spacing

The layout follows an airy, fluid-column grid structured around generous negative space:
- **Desktop:** 12-column fluid grid, `2.5rem` margins, and `1.5rem` gutters. Maximum content width caps at `1280px` to maintain focused proportions.
- **Tablet (768px - 1024px):** 8-column grid with `1.75rem` margins and `1.25rem` gutters.
- **Mobile (< 768px):** 4-column grid with `1.25rem` outer margins and `1rem` gutters.

Because Claymorphic surfaces rely on soft volumetric shadows, cards require wider element margins and internal padding (`space-md` to `space-lg`) to prevent optical overcrowding and ensure shadows disperse smoothly without clipping.

## Elevation & Depth

Visual depth is achieved through **sculptural claymorphism**: combining diffuse outer drop shadows with directional inner specular highlights and inset shadows to generate soft, 3D pillow volumes without hard bevels.

- **Ambient Drop Shadow:** Multi-stop, extra-diffused colored shadows. Instead of neutral black or grey, shadows use a tinted slate-periwinkle tone (`rgba(35, 62, 130, 0.08)` to `rgba(35, 62, 130, 0.14)`), preventing muddiness against pastel backgrounds.
- **Clay Top Inset Highlight:** An inner shadow along the top edge (`inset 0 3px 6px rgba(255, 255, 255, 0.8)`) mimics overhead soft light striking the curved surface.
- **Clay Bottom Inset Shadow:** An opposing inner shadow along the base (`inset 0 -4px 6px rgba(110, 140, 209, 0.12)`) rounds out the cushion effect.
- **Subtle Surface Stroke:** Surfaces employ a semi-translucent perimeter border (`1px solid rgba(255, 255, 255, 0.6)`) to crisp the boundary where the soft curve meets the canvas.
- **Interaction Elevation:**
  - *Default Card:* `box-shadow: 0 12px 28px rgba(35, 62, 130, 0.07), inset 0 2px 4px rgba(255, 255, 255, 0.9), inset 0 -3px 5px rgba(110, 140, 209, 0.1)`.
  - *Active / Pressed State:* Reduced outer shadow and deepened inset shadow (`inset 0 3px 6px rgba(35, 62, 130, 0.15)`), giving the sensation of physical compression into clay.

## Shapes

The shape system adopts a **Pill-shaped (level 3)** standard, amplifying the organic, smooth clay aesthetic.

- **Base Components (Inputs, Buttons, Badges):** Full pill geometry (`rounded-full` / `9999px`) provides consistent tactile appeal.
- **Cards and Containers:** Scaled using large, smooth radii (`rounded-2xl` at `1.5rem` / `24px` and `rounded-3xl` at `2rem` / `32px`) to mimic sculpted slabs with eased edges.
- **Modals & Overlays:** Bound by extra-generous radii (`2.5rem` / `40px`) to preserve a soft silhouette when floating over the main canvas.

## Components

### Buttons & Interactive Controls
- **Primary Buttons:** Pill-shaped (`rounded-full`), filled with `#4361EE` and white typography. Elevated with a dual shadow: an outer soft periwinkle glow (`0 8px 18px rgba(67, 97, 238, 0.3)`) paired with a light top inner glow (`inset 0 2px 3px rgba(255, 255, 255, 0.35)`). On press, the button translates down `1px` with compressed shadows.
- **Secondary Buttons:** Milky frosted white body (`#FFFFFF`), neutral navy text, with an ambient pastel shadow and a `1px` white border.
- **Pill Chips & Tags:** Compact pill shapes with subtle ice-blue fills (`#EBF1FC`), tinted navy labels, and micro-scale inner highlights. Active chips transition to solid `#4361EE` text and white background with a soft drop shadow.

### Input Fields & Search Bars
- Recessed or cushioned surface styling. The field sits within an ice-tinted fill (`#EAF0FA`) featuring a soft inset shadow (`inset 0 2px 4px rgba(35, 62, 130, 0.08)`) and no sharp borders.
- Focused states transition the container to `#FFFFFF` with an outer soft periwinkle halo (`0 0 0 3px rgba(67, 97, 238, 0.2)`).

### Cards & Panels
- **Standard Clay Card:** Frosted milk white background (`#FFFFFF`), `rounded-3xl`, padded with `space-lg`. Features dual inner shadows (top light, bottom dark) and a dispersed ambient blue drop shadow.
- **Hero / Highlight Card:** Pale periwinkle gradient wash (`linear-gradient(145deg, #FFFFFF, #E8F0FC)`) surrounded by a distinct bright white top highlight stroke.

### Checkboxes, Radio Buttons & Toggles
- **Toggles:** Pill-shaped track with inset depth (`#DDE6F7`). The thumb is a pristine white clay pebble with an ambient drop shadow that slides fluidly, turning `#4361EE` upon selection.
- **Checkboxes & Radios:** Curved squircle or circle containers with a delicate recessed shadow. Checked state fills with `#4361EE` and displays a bold white checkmark or dot.

### Specialized Components
- **Segmented Control Bar:** A pill-shaped trough with inset depth holding a sliding white clay pill indicator that smoothly transitions between states.
- **Floating Navigation Dock:** A pill-shaped bar anchored at screen edges with semi-transparent frosted milk white backing (`rgba(255, 255, 255, 0.85)` with `backdrop-filter: blur(16px)`), framed with clay elevation.