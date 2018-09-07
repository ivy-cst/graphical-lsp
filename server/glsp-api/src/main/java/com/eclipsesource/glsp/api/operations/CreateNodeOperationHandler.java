package com.eclipsesource.glsp.api.operations;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

import com.eclipsesource.glsp.api.action.ExecuteOperationAction;
import com.eclipsesource.glsp.api.action.kind.CreateNodeOperationAction;
import com.eclipsesource.glsp.api.factory.GraphicalModelState;
import com.eclipsesource.glsp.api.utils.SModelIndex;

import io.typefox.sprotty.api.Point;
import io.typefox.sprotty.api.SModelElement;
import io.typefox.sprotty.api.SModelRoot;

public abstract class CreateNodeOperationHandler implements OperationHandler {

	@Override
	public boolean handles(ExecuteOperationAction action) {
		return action instanceof CreateNodeOperationAction;
	}

	@Override
	public Optional<SModelRoot> execute(ExecuteOperationAction action, GraphicalModelState modelState) {
		CreateNodeOperationAction executeAction = (CreateNodeOperationAction) action;

		SModelIndex index = modelState.getCurrentModelIndex();

		SModelElement container = index.get(executeAction.getContainerId());
		if (container == null) {
			container = modelState.getCurrentModel();
		}

		Optional<Point> point = Optional.of(executeAction.getLocation());
		SModelElement element = createNode(point, index);
		if (container.getChildren() == null) {
			container.setChildren(new ArrayList<SModelElement>());
		}
		container.getChildren().add(element);
		index.addToIndex(element, container);
		return Optional.of(modelState.getCurrentModel());
	}

	protected String generateID(SModelElement element, SModelIndex index) {
		int i = index.getTypeCount(element.getType());
		String id = element.getType().replace(":", "").toLowerCase();

		while (index.get(id + i) != null) {
			i++;
		}
		return id + i;
	}

	protected abstract SModelElement createNode(Optional<Point> point, SModelIndex index);
	
	protected int getCounter(SModelIndex index, String type, Function<Integer, String> idProvider) {
		int i = index.getTypeCount(type);
		while (true) {
			String id = idProvider.apply(i);
			if (index.get(id) == null) {
				break;
			}
			i++;
		}
		return i;
	}

}