package org.opencloudb.front.sql;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class MycatParameters {

	private List<WarpParam> methodArgList;

	public MycatParameters() {
		super();
		methodArgList = new LinkedList<>();
	}

	/**
	 * @param methodName
	 * @param classType
	 * @param argInfo
	 */
	public void set(String methodName, Class<?>[] classType, Object[] argInfo) {
		methodArgList.add(new WarpParam(methodName, classType, argInfo));
	}

	public void clear() {
		methodArgList.clear();
	}

	public void fillPreparedStatement(PreparedStatement ps) throws SQLException {
		for (WarpParam e : methodArgList) {
			try {
				ps.getClass().getMethod(e.getMethodName(), e.getClassType())
						.invoke(ps, e.getArgInfo());
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException
					| SecurityException e1) {
				e1.printStackTrace();
			}
		}
	}

	private class WarpParam {
		private String methodName;
		private Class<?>[] classType;
		private Object[] argInfo;

		public WarpParam(String methodName, Class<?>[] classType,
				Object[] argInfo) {
			super();
			this.methodName = methodName;
			this.classType = classType;
			this.argInfo = argInfo;
		}

		public Class<?>[] getClassType() {
			return classType;
		}

		public Object[] getArgInfo() {
			return argInfo;
		}

		public String getMethodName() {
			return methodName;
		}

	}
}