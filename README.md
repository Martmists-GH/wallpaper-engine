# Linux Wallpaper Engine

This is a prototype for a wallpaper engine for KDE. It's currently very much in a prototype stage, and not recommended over [Wallpaper Engine KDE Plugin](https://github.com/catsout/wallpaper-engine-kde-plugin) for actual use.

## Comparison to Wallpaper Engine KDE Plugin

Pros:

- Supports nearly all effects from Wallpaper Engine, except for media data.
- Better compatibility with many effects.

Cons:

- Less configuration options.
- Runs as a separate process and window, meaning it obscures other windows. KWin rules can work around it to some degree, but it's not perfect.
- The cursor disappears when hovering over the wallpaper.
- Certain effects (not sure which) seem to flicker aggressively at times.

## Known issues

- Application was designed at 4k, experience may vary at other resolutions.
- Wayland display scaling is not supported.

## Planned features

- Support for wallpaper settings (will be stored as preset).
- Search/Filtering based on tags/type.
- Potential support for media data (?)

## Usage

On first launch, the application will show a menu. Any subsequent launches, it will apply the last selected wallpapers.    
To open the menu again, right-click the tray icon and select "Open Wallpaper Engine".    
To remove a wallpaper, right-click the tray icon and select "Remove Wallpaper" or "Remove from Display \<X>".


## Installation

### Setting up KWin rules

You'll want to add the following KWin rules:

---

Description: `Wallpaper Engine Windows`    
Window class: `Regular Expression`, `wallpaper(64|32).exe`    
Match whole window class: `Yes`    
Window types: `All selected`    

Keep below other windows: `Force`, `Yes`    
Skip taskbar: `Force`, `Yes`    
Skip pager: `Force`, `Yes`    
Skip switcher: `Force`, `Yes`    

No titlebar and frame: `Force`, `Yes`    
Focus stealing prevention: `Force`, `Extreme`    
Focus protection: `Force`, `Extreme`    
Accept focus: `Force`, `No`    
Closeable: `Force`, `No`    
Layer: `Force`, `Desktop`

---

(repeat this for each display, X starts at 1)

Description: `Wallpaper Engine Display <X>`    
Window class: `Regular Expression`, `wallpaper(64|32).exe`    
Match whole window class: `Yes`    
Window types: `All selected`    
Window title: `Exact match`, `Wallpaper Engine <X>`

Position: `Force`, `0,0`  (set to the top-left corner of the display, check the Display & Monitor tab)

---

### Installing the application

TODO (publishing not set up)

## License

This project is licensed under the [3-Clause BSD NON-AI License](https://github.com/non-ai-licenses/non-ai-licenses/blob/main/NON-AI-BSD3).
