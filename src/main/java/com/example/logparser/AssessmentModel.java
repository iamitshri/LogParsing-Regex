package com.example.logparser;

import java.util.List;

import lombok.Data;

@Data
public class AssessmentModel {
	private String studentId;
	private String assessmentCode;
	private String assessmentId;
	private List<TaskModel> tasks;
}