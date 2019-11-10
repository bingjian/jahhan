package net.jahhan.jdbc.event;

import net.jahhan.jdbc.SuperDO;

/**
 * 数据变更的event
 */
public class DataModifyEvent extends DBEvent {

	public DataModifyEvent(SuperDO<?> source, String dataSource, String type, String operate, String id) {
		super(source.clone(), dataSource, type, operate, id);
	}

	private static final long serialVersionUID = -8607165576941794121L;

}
