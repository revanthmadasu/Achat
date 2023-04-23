import { PubSub } from "@google-cloud/pubsub";
import { logger } from "firebase-functions/v1";

export class PubSubService {
    private static instance: PubSubService;
    private static MESSAGES_TOPIC = "messages_topic";
    private static DELAY_MINUTES = 100;
    public pubSubClient: PubSub;
    private constructor() {
        this.pubSubClient = new PubSub();
    }
    public static getPubSubService() {
        if (!PubSubService.instance) {
            PubSubService.instance = new PubSubService();
        }
        return PubSubService.instance;
    }

    public async publishMessage(data: any) {
        const dataString = JSON.stringify(data);
        const dataBuffer = Buffer.from(dataString);
        logger.log("Publishing data: ", dataBuffer);
        let messageId = "not received";
        try {
            const publishTime = new Date(Date.now() + PubSubService.DELAY_MINUTES * 60 * 1000).toISOString();
            messageId = await this.pubSubClient
                .topic(PubSubService.MESSAGES_TOPIC)
                .publishMessage({
                    data: dataBuffer,
                    attributes: {
                        publishTime
                    }
                });
            logger.log(`Message ${messageId} published.`);
        } catch (error: any) {
            logger.error(`Received error while publishing: ${error.message}`);        
            process.exitCode = 1;
        }
        return messageId;
    }
}