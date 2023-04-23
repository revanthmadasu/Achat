import { logger, pubsub } from "firebase-functions";
import { autoReply } from "./AutoReply";

export const autoreplySubscription = pubsub.topic('messages_topic').onPublish((message, context)=> {
    const messageObjStr = Buffer.from(message.data, 'base64').toString();
    const messageObj = JSON.parse(messageObjStr);
    logger.info("AutoreplySubscriber - Received Data: ", JSON.stringify(messageObj));
    return autoReply(messageObj);
});