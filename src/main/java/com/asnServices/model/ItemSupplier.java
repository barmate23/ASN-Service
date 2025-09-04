package com.asnServices.model;
// Online Java Compiler
// Use this editor to write, compile and run your Java code online

import java.util.*;
		import java.util.stream.Collectors;

public class ItemSupplier {
	private Integer itemId;
	private Integer supplierId;
	private Integer leadTime;
	private Boolean isDay;

	// Constructor
	public ItemSupplier(Integer itemId, Integer supplierId, Integer leadTime, Boolean isDay) {
		this.itemId = itemId;
		this.supplierId = supplierId;
		this.leadTime = leadTime;
		this.isDay = isDay;
	}

	// Get lead time in days
	public int getLeadTimeInDays() {
		return isDay ? leadTime : (leadTime + 23) / 24; // Convert hours to days, rounding up
	}



	// Getters
	public Integer getItemId() {
		return itemId;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public Integer getLeadTime() {
		return leadTime;
	}

	public Boolean getIsDay() {
		return isDay;
	}

	@Override
	public String toString() {
		return "ItemSupplier{" +
				"itemId=" + itemId +
				", supplierId=" + supplierId +
				", leadTime=" + leadTime +
				", isDay=" + isDay +
				'}';
	}
}
