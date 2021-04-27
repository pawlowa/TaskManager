package config.yaml;

public class CustomerConfig {

    private String name;
    private TaskManager taskmanager;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public TaskManager getTaskmanager() {
        return taskmanager;
    }
    public void setTaskmanager(TaskManager taskmanager) {
        this.taskmanager = taskmanager;
    }

}
