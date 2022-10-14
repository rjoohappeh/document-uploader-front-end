package com.fdmgroup.documentuploader.model.account.servicelevel;

import java.util.Comparator;

import org.springframework.stereotype.Component;

/**
 * Comparator which when used will cause {@linkplain ServiceLevel} objects to be sorted
 * within a sorted collection based on their price.
 * 
 * @author Noah Anderson
 * @author Roy Coates
 *
 */
@Component
public class ServiceLevelComparator implements Comparator<ServiceLevel>{

	@Override
	public int compare(ServiceLevel o1, ServiceLevel o2) {
		return o1.getPrice().compareTo(o2.getPrice());
	}

}
