package at.tortmayr.chillisp.api.actions;

import at.tortmayr.chillisp.api.IAction;

public abstract class Action implements IAction {

	private String kind;

	public Action(String kind) {
		super();
		this.kind = kind;
	}

	@Override
	public String getKind() {
		return kind;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Action [kind=");
		builder.append(kind);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Action other = (Action) obj;
		if (kind == null) {
			if (other.kind != null)
				return false;
		} else if (!kind.equals(other.kind))
			return false;
		return true;
	}

}
