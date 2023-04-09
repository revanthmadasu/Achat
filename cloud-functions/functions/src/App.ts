import { initializeApp, App } from "firebase-admin/app";

export class AchatFirebaseApp {
    private static instance: App;
    private constructor() {

    }
    public static getApp() {
        if (!AchatFirebaseApp.instance) {
            AchatFirebaseApp.instance = initializeApp();
        }
        return AchatFirebaseApp.instance
    }
}