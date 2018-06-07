package com.example.logparser;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;

@Data
public class TaskModel {
	private String submissionId;
	private String evaluatorId;
	private String taskId;
	private String taskName;
	private Integer status;
	private Date dateUpdated;
	LocalDateTime finishedProcessingTime;
	private int number;
}
