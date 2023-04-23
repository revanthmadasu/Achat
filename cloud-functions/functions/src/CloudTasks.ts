import { CloudTasksClient } from "@google-cloud/tasks";
import { logger } from "firebase-functions/v1";
import { CONSTANTS } from "./constants";


export class CloudTasksService {
    private static instance: CloudTasksService;

    public cloudTasksClient: CloudTasksClient;
    private constructor() {
        this.cloudTasksClient = new CloudTasksClient();
    }
    public static getCloudTasksService() {
        if (!CloudTasksService.instance) {
            CloudTasksService.instance = new CloudTasksService();
            CloudTasksService.instance.cloudTasksClient = new CloudTasksClient();
        }
        return CloudTasksService.instance;
    }
    public async addTaskToQueue(payload: any) {
        logger.log('In addTaskToQueue');
        const task: any = {
          httpRequest: {
            httpMethod: "POST",
            url: CONSTANTS.AUTO_REPLY_HTTPS_API,
            headers: {
              'Content-Type': 'application/json',
            },
            body: Buffer.from(JSON.stringify(payload)).toString('base64'),
            bodyEncoding: 'base64',
          },
          scheduleTime: {
            seconds: Math.floor(Date.now() / 1000) + (CONSTANTS.AUTO_REPLY_DELAY_MINS * 60),
          },
        };
      
        const parent = this.cloudTasksClient.queuePath(CONSTANTS.PROJECT_ID, CONSTANTS.PROJECT_LOCATION, CONSTANTS.MESSAGES_QUEUE);
        const [response] = await this.cloudTasksClient.createTask({ parent, task });
      
        logger.log(`Created task ${response.name}`);
        return response;
    }

    public async deleteTask(taskName: string) {
        logger.log(`Attempting to delete queue task ${taskName}`);
        const res = await this.cloudTasksClient.deleteTask({
            name: this.cloudTasksClient.taskPath(CONSTANTS.PROJECT_ID, CONSTANTS.PROJECT_LOCATION, CONSTANTS.MESSAGES_QUEUE, taskName)
        });
        logger.log(`deleted queue task ${taskName}`);
        return res;
    }
}