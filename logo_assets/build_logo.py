import os
from PIL import Image, ImageDraw

def generate_svg():
    # 512x512 Pure Vector SVG following exact specifications
    svg = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512" width="512" height="512">
  <defs>
    <!-- Clip path for the yellow sticker body (squircle minus top-right folded corner) -->
    <clipPath id="yellow-sticker-clip">
      <!-- Outer squircle -->
      <path d="M 160 86 
               L 310 86 
               L 426 202 
               L 426 352 
               A 74 74 0 0 1 352 426 
               L 160 426 
               A 74 74 0 0 1 86 352 
               L 86 160 
               A 74 74 0 0 1 160 86 Z" />
    </clipPath>
  </defs>

  <!-- Background: Solid deep purple #5B21B6 -->
  <rect width="512" height="512" fill="#5B21B6" />

  <!-- White Die-Cut Border: Outer Squircle with peeled top-right contour -->
  <!-- Thickness ~ 14px surrounding the yellow sticker -->
  <path d="M 152 72
           L 312 72
           L 440 200
           L 440 360
           A 88 88 0 0 1 352 440
           L 160 440
           A 88 88 0 0 1 72 352
           L 72 160
           A 88 88 0 0 1 160 72 Z"
        fill="#FFFFFF" />

  <!-- Main Yellow Squircle Sticker Body: #FFC72C -->
  <path d="M 160 86
           L 310 86
           L 426 202
           L 426 352
           A 74 74 0 0 1 352 426
           L 160 426
           A 74 74 0 0 1 86 352
           L 86 160
           A 74 74 0 0 1 160 86 Z"
        fill="#FFC72C" />

  <!-- Folded Corner (Peel): Golden-Yellow #CA8A04 Triangle Flap (underside) -->
  <path d="M 310 86
           L 426 202
           L 326 202
           A 16 16 0 0 1 310 186 Z"
        fill="#CA8A04" />
  <!-- Fold crease highlight / die-cut edge for the peel -->
  <path d="M 310 86 L 426 202" stroke="#FFFFFF" stroke-width="4" stroke-linecap="round" />

  <!-- Face Elements (Playful, friendly winking face in deep indigo #1E1B4B) -->
  
  <!-- Left Eye: Solid circular dark dot -->
  <circle cx="196" cy="242" r="21" fill="#1E1B4B" />

  <!-- Cheek: Solid orange circle #FF6B35 below and outward from left eye -->
  <circle cx="156" cy="296" r="26" fill="#FF6B35" />

  <!-- Right Eye: Smooth single-stroke curved wink arc -->
  <path d="M 276 248 Q 302 218 328 248" 
        stroke="#1E1B4B" stroke-width="15" stroke-linecap="round" stroke-linejoin="round" fill="none" />

  <!-- Mouth: Broad, friendly upward-curving smile -->
  <path d="M 204 316 Q 262 366 322 298" 
        stroke="#1E1B4B" stroke-width="15" stroke-linecap="round" stroke-linejoin="round" fill="none" />
  <!-- Smile dimple / playful flourish -->
  <path d="M 320 286 L 328 304" 
        stroke="#1E1B4B" stroke-width="13" stroke-linecap="round" fill="none" />
</svg>
'''
    return svg

def generate_foreground_svg():
    # Adaptive Icon Foreground (Transparent background, centered in 108dp canvas)
    svg = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512" width="512" height="512">
  <!-- White Die-Cut Border -->
  <path d="M 152 72
           L 312 72
           L 440 200
           L 440 360
           A 88 88 0 0 1 352 440
           L 160 440
           A 88 88 0 0 1 72 352
           L 72 160
           A 88 88 0 0 1 160 72 Z"
        fill="#FFFFFF" />

  <!-- Main Yellow Squircle Sticker Body: #FFC72C -->
  <path d="M 160 86
           L 310 86
           L 426 202
           L 426 352
           A 74 74 0 0 1 352 426
           L 160 426
           A 74 74 0 0 1 86 352
           L 86 160
           A 74 74 0 0 1 160 86 Z"
        fill="#FFC72C" />

  <!-- Folded Corner Flap: #CA8A04 -->
  <path d="M 310 86
           L 426 202
           L 326 202
           A 16 16 0 0 1 310 186 Z"
        fill="#CA8A04" />
  <path d="M 310 86 L 426 202" stroke="#FFFFFF" stroke-width="4" stroke-linecap="round" />

  <!-- Face Elements -->
  <circle cx="196" cy="242" r="21" fill="#1E1B4B" />
  <circle cx="156" cy="296" r="26" fill="#FF6B35" />
  <path d="M 276 248 Q 302 218 328 248" 
        stroke="#1E1B4B" stroke-width="15" stroke-linecap="round" stroke-linejoin="round" fill="none" />
  <path d="M 204 316 Q 262 366 322 298" 
        stroke="#1E1B4B" stroke-width="15" stroke-linecap="round" stroke-linejoin="round" fill="none" />
  <path d="M 320 286 L 328 304" 
        stroke="#1E1B4B" stroke-width="13" stroke-linecap="round" fill="none" />
</svg>
'''
    return svg

def generate_android_vector_drawable():
    # Android Vector Drawable XML (108dp x 108dp for Adaptive Icons)
    xml = '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="512"
    android:viewportHeight="512">
    <!-- White Die-Cut Border -->
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M 152,72 L 312,72 L 440,200 L 440,360 A 88,88 0 0,1 352,440 L 160,440 A 88,88 0 0,1 72,352 L 72,160 A 88,88 0 0,1 160,72 Z" />
    
    <!-- Yellow Sticker Body -->
    <path
        android:fillColor="#FFC72C"
        android:pathData="M 160,86 L 310,86 L 426,202 L 426,352 A 74,74 0 0,1 352,426 L 160,426 A 74,74 0 0,1 86,352 L 86,160 A 74,74 0 0,1 160,86 Z" />
    
    <!-- Folded Corner Flap -->
    <path
        android:fillColor="#CA8A04"
        android:pathData="M 310,86 L 426,202 L 326,202 A 16,16 0 0,1 310,186 Z" />
    
    <!-- Fold Crease Outline -->
    <path
        android:strokeColor="#FFFFFF"
        android:strokeWidth="4"
        android:strokeLineCap="round"
        android:pathData="M 310,86 L 426,202" />
    
    <!-- Left Eye -->
    <path
        android:fillColor="#1E1B4B"
        android:pathData="M 196,221 A 21,21 0 1,0 196,263 A 21,21 0 1,0 196,221 Z" />
    
    <!-- Left Cheek -->
    <path
        android:fillColor="#FF6B35"
        android:pathData="M 156,270 A 26,26 0 1,0 156,322 A 26,26 0 1,0 156,270 Z" />
    
    <!-- Wink Eye -->
    <path
        android:strokeColor="#1E1B4B"
        android:strokeWidth="15"
        android:strokeLineCap="round"
        android:pathData="M 276,248 Q 302,218 328,248" />
    
    <!-- Smile -->
    <path
        android:strokeColor="#1E1B4B"
        android:strokeWidth="15"
        android:strokeLineCap="round"
        android:pathData="M 204,316 Q 262,366 322,298" />
    
    <!-- Smile Tip -->
    <path
        android:strokeColor="#1E1B4B"
        android:strokeWidth="13"
        android:strokeLineCap="round"
        android:pathData="M 320,286 L 328,304" />
</vector>
'''
    return xml

if __name__ == '__main__':
    out_dir = r'c:\Users\ASUS\AndroidStudioProjects\StickerMaker\logo_assets'
    os.makedirs(out_dir, exist_ok=True)
    
    with open(os.path.join(out_dir, 'ic_launcher_vector.svg'), 'w', encoding='utf-8') as f:
        f.write(generate_svg())
        
    with open(os.path.join(out_dir, 'ic_launcher_foreground.svg'), 'w', encoding='utf-8') as f:
        f.write(generate_foreground_svg())
        
    with open(os.path.join(out_dir, 'ic_launcher_foreground_vector.xml'), 'w', encoding='utf-8') as f:
        f.write(generate_android_vector_drawable())

    with open(os.path.join(out_dir, 'ic_launcher_background.xml'), 'w', encoding='utf-8') as f:
        f.write('''<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="#5B21B6"
        android:pathData="M0,0h108v108h-108z"/>
</vector>
''')

    print("All vector assets generated successfully.")
