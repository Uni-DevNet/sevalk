# SevaLK Stripe Payment Integration - Implementation Guide

This guide provides complete setup instructions for integrating Stripe payment processing into the SevaLK mobile application with a Node.js backend.

## 🏗️ Architecture Overview

```
┌─────────────────┐    HTTP/HTTPS    ┌─────────────────┐    Stripe API    ┌─────────────────┐
│                 │ ◄──────────────► │                 │ ◄──────────────► │                 │
│  Android App    │                  │  Node.js        │                  │     Stripe      │
│  (Kotlin)       │                  │  Backend        │                  │   Dashboard     │
│                 │                  │                 │                  │                 │
└─────────────────┘                  └─────────────────┘                  └─────────────────┘
         │                                      │
         ▼                                      ▼
┌─────────────────┐                  ┌─────────────────┐
│                 │                  │                 │
│   Firebase      │ ◄──────────────► │   Firebase      │
│   (Client SDK)  │                  │   (Admin SDK)   │
│                 │                  │                 │
└─────────────────┘                  └─────────────────┘
```

## 📱 Android App Changes

### 1. Dependencies Added
```kotlin
// Stripe Android SDK
implementation("com.stripe:stripe-android:21.21.0")
implementation("com.stripe:financial-connections:21.21.0")

// Retrofit for API calls
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
```

### 2. New Files Created
- `StripeModels.kt` - Data models for API requests/responses
- `PaymentApiService.kt` - Retrofit interface for backend API
- `StripePaymentViewModel.kt` - ViewModel for payment processing
- `PaymentScreenCard.kt` - Updated UI with Stripe PaymentSheet

### 3. Updated Files
- `NetworkModule.kt` - Added Retrofit and API service DI
- `RepositoryModule.kt` - Updated PaymentRepository binding
- `PaymentRepository.kt` - Added Stripe integration methods
- `BookingDetailsScreen.kt` - Updated navigation to new payment screen

## 🚀 Backend Setup

### 1. Prerequisites
- Node.js 18+ installed
- Stripe account (get free test keys)
- Firebase project with Firestore enabled

### 2. Backend Installation
```bash
cd sevalk-payment-backend
npm install
```

### 3. Environment Configuration
1. Copy `.env.example` to `.env`
2. Update with your actual values:
```bash
STRIPE_SECRET_KEY=sk_test_your_actual_secret_key
STRIPE_PUBLISHABLE_KEY=pk_test_your_actual_publishable_key
STRIPE_WEBHOOK_SECRET=whsec_your_webhook_secret
PORT=3000
FIREBASE_PROJECT_ID=your_firebase_project_id
```

### 4. Firebase Setup
1. Go to Firebase Console → Project Settings → Service Accounts
2. Click "Generate new private key"
3. Save as `firebase-service-account.json` in backend root directory

### 5. Start Backend Server
```bash
npm run dev
# or for production
npm start
```

## 🔑 Stripe Dashboard Setup

### 1. Get API Keys
1. Visit [Stripe Dashboard](https://dashboard.stripe.com/apikeys)
2. Copy **Publishable key** and **Secret key**
3. Use test keys for development (start with `pk_test_` and `sk_test_`)

### 2. Enable Payment Methods
1. Go to [Payment Methods Settings](https://dashboard.stripe.com/settings/payment_methods)
2. Enable **Cards** and any other desired methods

### 3. Create Webhook Endpoint (Optional but Recommended)
1. Go to [Webhooks](https://dashboard.stripe.com/webhooks)
2. Click "Add endpoint"
3. URL: `http://your-server-url/webhook/stripe`
4. Select events: `payment_intent.succeeded`, `payment_intent.payment_failed`
5. Copy webhook secret to your `.env` file

## 📱 Testing the Integration

### 1. Test Cards
Use these Stripe test cards:
- **Success**: `4242 4242 4242 4242`
- **Requires 3D Secure**: `4000 0025 0000 3155`
- **Declined**: `4000 0000 0000 9995`

### 2. Test Flow
1. Start backend server: `npm run dev`
2. Run Android app
3. Navigate to booking details with IN_PROGRESS status
4. Click "Proceed to Payment"
5. Test both card and cash payment options

## 🔧 Configuration

### Android App Configuration
Update `NetworkModule.kt` base URL if needed:
```kotlin
.baseUrl("http://10.0.2.2:3000/") // Android emulator
// or
.baseUrl("http://192.168.1.100:3000/") // Physical device
```

### Backend CORS Configuration
Update allowed origins in `server.js`:
```javascript
const allowedOrigins = [
  'http://localhost:3000',
  'http://10.0.2.2:3000', // Android emulator
  'http://192.168.1.100:3000' // Your local IP
];
```

## 🎯 Payment Flow

### Card Payment Flow
1. User selects "Credit/Debit Card"
2. App calls `/api/payments/create-intent`
3. Backend creates Stripe PaymentIntent
4. App receives `client_secret` and `publishable_key`
5. Stripe PaymentSheet is presented
6. User completes payment with Stripe
7. App calls `/api/payments/confirm`
8. Backend updates booking status to COMPLETED
9. Payment status set to COMPLETED

### Cash Payment Flow
1. User selects "Cash Payment"
2. App calls `/api/payments/cash`
3. Backend immediately updates booking status to COMPLETED
4. Payment status set to COMPLETED

## 🛡️ Security Features

### Backend Security
- Rate limiting (100 requests per 15 minutes)
- CORS protection
- Helmet security headers
- Input validation
- Stripe webhook signature verification

### Android Security
- HTTPS-only API calls
- Stripe client-side encryption
- No sensitive data stored locally

## 🐛 Troubleshooting

### Common Issues

1. **"Network error" in app**
   - Check backend server is running
   - Verify base URL in NetworkModule
   - Check device/emulator network connectivity

2. **"Firebase initialization error"**
   - Verify `firebase-service-account.json` exists
   - Check `FIREBASE_PROJECT_ID` in `.env`
   - Ensure Firebase Admin SDK permissions

3. **"Stripe key not found"**
   - Verify `.env` file exists and has correct keys
   - Check Stripe keys are test keys for development
   - Restart backend server after env changes

4. **Payment fails silently**
   - Check Stripe Dashboard → Logs for errors
   - Verify webhook endpoint is reachable
   - Check backend console for error logs

### Debug Mode
Enable debug logging in Android app:
```kotlin
// In NetworkModule.kt
HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY // Shows full request/response
}
```

## 🚀 Production Deployment

### Backend Deployment
1. Use production Stripe keys
2. Set `NODE_ENV=production`
3. Use HTTPS for webhook endpoints
4. Deploy to cloud provider (Heroku, AWS, GCP)
5. Update CORS origins for production domains

### Android App
1. Update base URL to production backend
2. Test with live Stripe keys
3. Enable ProGuard/R8 for release builds

## 📞 Support

- **Stripe Documentation**: https://stripe.com/docs
- **Firebase Documentation**: https://firebase.google.com/docs
- **Android Stripe SDK**: https://github.com/stripe/stripe-android

## 🎉 Success Indicators

✅ Backend health check responds at `/health`  
✅ Payment intent creation works  
✅ Stripe PaymentSheet displays correctly  
✅ Card payments complete successfully  
✅ Cash payments process immediately  
✅ Booking status updates to COMPLETED  
✅ Payment records saved in Firestore  

---

**Congratulations! 🎉 Your SevaLK app now has a complete Stripe payment integration with both card and cash payment options.**
