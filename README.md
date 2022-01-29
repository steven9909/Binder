## Work Distribution

| Person | Role |
| - | - |
| Anurag Vinodh | Video & Audio Call |
| Steven Hyun | Android Architecture Design & Development |
| Eric Kim | Backend and API |
| Benny Huang | Ranking Algorithm & Graph Neural Networks |
| Tony Tong | Android Development |
| Joey Chou | Android Development |

## What needs to be done?

- [ ] Frontend - S, J & T
	- [ ] Screens (Implemented in Android)
		- [x] Login
		- [x] Signup
		- [x] Main Dashboard
		- [ ] Calendar - T
			- [x] UI: Daily Calendar View
			- [x] Sync with Backend
			- [ ] Sync with multiple individuals for group (with the new layer page) - T (01/31/2022)
			- [x] Connect daily to monthly calendar
		- [ ] Main Page List of Calendar Events
		- [ ] Messaging
			- [x] UI
			- [x] Sync with Backend
			- [x] Load More Messages
			- [ ] Deeplink integration with notification - S/E (pending notification)
		- [x] File Sharing
		- [x] Quizzing/polling feature in messaging
		  - [x] Save quiz question and answers to db for crowd sourced questions
		- [x] Create Group Page
		- [ ] User Settings Page - J (01/31/2022)
		- [x] Edit User Profile Page
		- [ ] Friend
			- [x] UI
			- [x] Social
			- [x] Add Friends
			- [x] Recommended Friends
			- [x] Friends Request
				- [ ] Deeplink integration with notification - S/E (pending notification)
		- [x] Video & Audio Conferencing
			- [x] UI
			- [x] Buttons and functionality
- [ ] Backend (Firebase) - E
	- [x] Realtime Database
		- [x] Messaging
	- [ ] Firestore 
		- [x] Groups
		- [x] Friends
		- [x] Calendar Events
		- [x] User Info
		- [x] User Settings
		- [ ] more stuff (Waiting for API Function add/change request)
	- [x] Authentication
- [ ] Backend (Video & Audio Call)
	- [x] 100ms
		- [x] Audio
		- [x] Video
		- [x] Backend Tokenization
		- [ ] Voice Priority - A (01/31/2022)
		- [ ] Bottom sheet messaging - A (01/31/2022)
- [ ] Backend (Push Notification) - B
	- [x] Listener on database
	- [ ] Send Notification to Users - B
	- [x] Messages
	- [x] Friend Requests
- [ ] Backend (Recommender System) - B (before presentation date 02/18/2022)
- [ ] Cleanup and revamp UI