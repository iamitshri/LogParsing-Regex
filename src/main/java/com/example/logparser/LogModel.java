package com.example.logparser;

import java.time.LocalDateTime;

import edu.wgu.dreamcatcher.domain.model.AssessmentModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogModel {
	LocalDateTime dateOfLogEntry;
	AssessmentModel model;
	
//	dateOfLogEntry, studentId, assessmentCode, submissionId, taskId, taskName, status, dateUpdated <- from the TaskModel record
}
