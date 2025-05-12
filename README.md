# DelhiTravelApp (DTA)

# Problem Statement: 
Commuters in Delhi face challenges when navigating the city’s extensive metro network, especially in obtaining accurate, real-time information about metro arrivals, route interchanges. Existing official solutions often lack comprehensive features—such as accessible design, reliable next-train arrival at any chosen station, or easy to use UI/UX that can enhance the overall travel experience.

-> Through research, app flow analysis, and iterative testing, we crafted a truly user-centric design, prioritizing their needs and preferences, resulting in an improved and more engaging app experience. We did'nt tried to include all the things(ticketing ,card recharge,..etc) in this version. we focused on demostrating the main & required feature in this version.

# FUNCTIONALITIES : 
-> Multiple Activities/Fragments: The app has several activities including HomeActivity, RoutesActivity, HistoryActivity, and OptionActivity with multiple composable screen sections.
-> Database Connectivity: The app uses a local database for storing station data and route information through the DatabaseModule and StationRepository.
-> Caching of Data: Station data is cached locally for offline use.
-> Error Handling: Error messages are displayed for various scenarios like missing permissions, TTS initialization failures, etc.


# Features*:

*Accessibility Features*:
Text-to-Speech (TTS) & Haptic Feedbacksupport for visually impaired users
Content descriptions for screen readers
User can choose "Disabled Person" mode in OptionActivity

*Native API Usage*:
Location API for tracking user movement between stations

*Sensing*:
Location sensing to detect user position in metro network
Simulated movement between stations with notifications

*Unique User Interface*:
Custom expandable/collapsible metro line cards with visual route indicators
Animated clock display with real-time updates
Interactive filter chips for station selection
Direction selection buttons with visual feedback
Color-coded line indicators matching official Delhi Metro colors

(*things could be updated with upcoming version)
