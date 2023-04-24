import { logger, https } from "firebase-functions";
import { autoReply } from "./AutoReply";
import { CloudTasksService } from "./CloudTasks";

export const autoReplyApi = https.onRequest((req, res) => {
    logger.log("Received api call on autoreply api");
    const { body } = req;
    const projectId = req.get('X-CloudTasks-ProjectId');
    const queueName = req.get('X-CloudTasks-QueueName');
    const taskName = req.get('X-CloudTasks-TaskName');

    logger.info(`project id from req: ${projectId}`);
    logger.info(`queueName from req: ${queueName}`);
    logger.info(`taskName from req: ${taskName}`);

    const cloudTasksService = CloudTasksService.getCloudTasksService();

    cloudTasksService.deleteTask(taskName || "").then((res) => {
        logger.log("Successfully deleted task. res: ", res);
    }).catch(error => {
        logger.error("Error in deleting task: ", JSON.stringify(error));
    });
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
