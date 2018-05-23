package at.tortmayr.chillisp.api.actions;

import io.typefox.sprotty.api.Point;

public class ExecuteNodeCreationToolAction extends Action {

	private String toolId;
	private Point location;
	private String containerId;

	public ExecuteNodeCreationToolAction() {
		super(Action.Kind.EXECUTE_NODE_CREATION_TOOL);
	}

	public ExecuteNodeCreationToolAction(String toolId, Point location, String containerId) {
		this();
		this.toolId = toolId;
		this.location = location;
		this.containerId = containerId;
	}

	public String getToolId() {
		return toolId;
	}

	public Point getLocation() {
		return location;
	}

	public String getContainerId() {
		return containerId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((containerId == null) ? 0 : containerId.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((toolId == null) ? 0 : toolId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExecuteNodeCreationToolAction other = (ExecuteNodeCreationToolAction) obj;
		if (containerId == null) {
			if (other.containerId != null)
				return false;
		} else if (!containerId.equals(other.containerId))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (toolId == null) {
			if (other.toolId != null)
				return false;
		} else if (!toolId.equals(other.toolId))
			return false;
		return true;
	}

}
