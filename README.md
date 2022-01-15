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
			- [ ] Sync with multiple individuals for group (with the new layer page) - T (01/19/2022)
			- [ ] Connect daily to monthly calendar - T (01/16/2022)
		- [ ] Main Page List of Calendar Events
		- [ ] Messaging - E
			- [x] UI
			- [x] Sync with Backend
			- [ ] Load More Messages - E (01/16/2022)
			- [ ] Deeplink integration with notification - S/E (01/19/2022)
		- [ ] File Sharing - S (01/24/2022)
		- [ ] Create Group Page - J (01/16/2022)
		- [ ] User Settings Page - J (01/19/2022)
		- [ ] Edit User Profile Page - J (01/21/2022)
		- [ ] Friend
			- [x] UI
			- [ ] Social - S
				- [ ] Delete on swipe - S (01/16/2022)
				- [x] Group List Content
			- [x] Add Friends
			- [ ] Recommended Friends - J (01/19/2022)
			- [x] Friends Request
				- [ ] Deeplink integration with notification - S/E (01/19/2022)
		- [ ] Video & Audio Conferencing - A
			- [x] UI
			- [ ] Buttons and functionality - A (01/19/2022)
- [ ] Backend (Firebase) - E
	- [x] Realtime Database
		- [x] Messaging
	- [ ] Firestore 
		- [ ] Groups (Waiting for API Function add/change request)
		- [x] Friends
		- [x] Calendar Events
		- [x] User Info
		- [x] User Settings
		- [ ] more stuff (Waiting for API Function add/change request)
	- [x] Authentication
- [ ] Backend (Video & Audio Call) - A
	- [x] 100ms
		- [x] Audio
		- [x] Video
		- [ ] Backend Tokenization - A (01/16/2022)
		- [ ] Voice Priority - A (01/21/2022)
		- [ ] Conferencing Tools (Note Sharing) - A (01/24/2022)
- [ ] Backend (Push Notification) - B
	- [x] Listener on database
	- [ ] Send Notification to Users - B (01/16/2022)
	- [x] Messages
	- [x] Friend Requests
- [ ] Backend (Recommender System) - B (before presentation date 02/18/2022)
