import { logger, https } from "firebase-functions";
import { autoReply } from "./AutoReply";

export const autoReplyApi = https.onRequest((req, res) => {
    logger.log("Received api call on autoreply api");
    const { body } = req;
    logger.info(`Received body: ${body}`);
    try {
        logger.info(`Received body: ${JSON.stringify(body)}`);
    } catch(e) {
        logger.error(`error in parsing: ${JSON.stringify(e)}`);
    }
    autoReply(body).then((result) => {
        logger.info(`Received result from autoreply function: ${JSON.stringify(result)}`);
        res.status(result.status).send(result);
    }).catch(error => {
        logger.error(`Received error from autoreply function: ${JSON.stringify(error)}`);
        res.status(500).send(error);
    });
});
