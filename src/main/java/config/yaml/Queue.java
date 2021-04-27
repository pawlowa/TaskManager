package config.yaml;

import queues.QueueTypeEnum;

public class Queue {

    private QueueTypeEnum type;
    private int maxRunningCapacity;

    public QueueTypeEnum getType() {
        return type;
    }
    public void setType(QueueTypeEnum type) {
        this.type = type;
    }
    public int getMaxRunningCapacity() {
        return maxRunningCapacity;
    }
    public void setMaxRunningCapacity(int maxRunningCapacity) {
        this.maxRunningCapacity = maxRunningCapacity;
    }

}
