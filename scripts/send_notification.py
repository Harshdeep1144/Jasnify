import firebase_admin
from firebase_admin import credentials, messaging, firestore
import os

# 1. Download your service account key from Firebase Console:
# Project Settings -> Service Accounts -> Generate new private key
# 2. Place it in the root directory or the scripts folder and rename it to 'service-account.json'
# We check both locations for convenience.
SERVICE_ACCOUNT_PATH = "service-account.json"
if not os.path.exists(SERVICE_ACCOUNT_PATH):
    SERVICE_ACCOUNT_PATH = os.path.join(os.path.dirname(__file__), "service-account.json")

if not os.path.exists(SERVICE_ACCOUNT_PATH):
    print("Error: 'service-account.json' not found. Please place it in the root or scripts directory.")
    exit(1)

cred = credentials.Certificate(SERVICE_ACCOUNT_PATH)
firebase_admin.initialize_app(cred)
db = firestore.client()

def setup_welcome_notification():
    """Creates the welcome notification template in Firestore"""
    notification_id = "welcome_notification"
    doc_ref = db.collection("notifications").document(notification_id)

    config = {
        "title": "Welcome to Jasnify! 🚀",
        "body": "Thank you for joining us. Start exploring your personalized dashboard now!",
        "uiType": "bigText",
        "priority": 2, # High priority
        "deepLink": "https://jasnify.com/home",
        "imageUrl": None, # Add a URL here if you want a big picture
        "backgroundColor": "#557373"
    }

    doc_ref.set(config)
    print(f"Firestore template '{notification_id}' created/updated.")
    return notification_id

def send_to_all(notification_id):
    """Sends a trigger message to the 'all' topic"""
    message = messaging.Message(
        data={
            'notification_id': notification_id,
        },
        topic='all',
    )

    response = messaging.send(message)
    print(f"Successfully sent message to 'all' topic. Response: {response}")

def setup_promo_notification():
    """Creates a promotional notification template with an image"""
    notification_id = "promo_notification"
    doc_ref = db.collection("notifications").document(notification_id)
    
    config = {
        "title": "New Collection is Here! 👗",
        "body": "Check out our latest outfits for your big day. Limited time offer!",
        "uiType": "bigPicture",
        "priority": 2,
        "deepLink": "https://jasnify.com/outfits",
        "imageUrl": "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?q=80&w=1000",
        "backgroundColor": "#557373"
    }
    
    doc_ref.set(config)
    print(f"Firestore template '{notification_id}' created/updated.")
    return notification_id

if __name__ == "__main__":
    # 1. Send Welcome Notification
    welcome_id = setup_welcome_notification()
    send_to_all(welcome_id)
    
    # 2. Send Promo Notification with Image
    promo_id = setup_promo_notification()
    send_to_all(promo_id)
