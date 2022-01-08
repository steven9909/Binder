package service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // logic here
    }

    override fun onNewToken(token: String) {
        uploadRegistrationTokenToFirestore(token)
    }

    private fun uploadRegistrationTokenToFirestore(token: String) {
        // TODO: Eric implement this
    }
}
