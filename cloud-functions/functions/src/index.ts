import { initializeApp } from "firebase-admin/app";
import { onMessageSent } from "./MessageListener";
initializeApp();

export { onMessageSent }
// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript
//
// export const helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });
