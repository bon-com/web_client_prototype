package com.example.web_client_prototype.resource;


import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * REST APIにて返却するリソースオブジェクト
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Resource {
	/** ID */
	private String id;
	/** 名前 */
	private String name;
	/** とある日付 */
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate hogeDate;
}