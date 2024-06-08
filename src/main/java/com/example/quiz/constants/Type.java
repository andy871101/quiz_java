package com.example.quiz.constants;

public enum Type {

	SINGLE_CHOICE("Single choice"), //
	MULTI_CHOICE("Multi choice"), //
	TEXT("Text");

	private String type;

	private Type(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
