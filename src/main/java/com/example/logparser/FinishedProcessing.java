package com.example.logparser;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinishedProcessing {

	// Date of Processing finished message
	LocalDateTime dateTimefinishedProcessing;

	// Submission Id
	String submissionId;

	// Status
	String status;
}
