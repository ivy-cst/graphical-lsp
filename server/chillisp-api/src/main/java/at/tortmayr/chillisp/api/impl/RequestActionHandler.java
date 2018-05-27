package at.tortmayr.chillisp.api.impl;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import at.tortmayr.chillisp.api.IGraphicalLanguageServer;
import at.tortmayr.chillisp.api.IGraphicalModelExpansionListener;
import at.tortmayr.chillisp.api.IGraphicalModelSelectionListener;
import at.tortmayr.chillisp.api.IGraphicalModelState;
import at.tortmayr.chillisp.api.IModelElementOpenListener;
import at.tortmayr.chillisp.api.IRequestActionHandler;
import at.tortmayr.chillisp.api.LayoutUtil;
import at.tortmayr.chillisp.api.actions.CenterAction;
import at.tortmayr.chillisp.api.actions.ChangeBoundsAction;
import at.tortmayr.chillisp.api.actions.CollapseExpandAction;
import at.tortmayr.chillisp.api.actions.CollapseExpandAllAction;
import at.tortmayr.chillisp.api.actions.ComputedBoundsAction;
import at.tortmayr.chillisp.api.actions.ExecuteNodeCreationToolAction;
import at.tortmayr.chillisp.api.actions.ExecuteToolAction;
import at.tortmayr.chillisp.api.actions.FitToScreenAction;
import at.tortmayr.chillisp.api.actions.MoveAction;
import at.tortmayr.chillisp.api.actions.OpenAction;
import at.tortmayr.chillisp.api.actions.RequestBoundsAction;
import at.tortmayr.chillisp.api.actions.RequestBoundsChangeHintsAction;
import at.tortmayr.chillisp.api.actions.RequestExportSvgAction;
import at.tortmayr.chillisp.api.actions.RequestLayersAction;
import at.tortmayr.chillisp.api.actions.RequestModelAction;
import at.tortmayr.chillisp.api.actions.RequestMoveHintsAction;
import at.tortmayr.chillisp.api.actions.RequestPopupModelAction;
import at.tortmayr.chillisp.api.actions.RequestToolsAction;
import at.tortmayr.chillisp.api.actions.SelectAction;
import at.tortmayr.chillisp.api.actions.SelectAllAction;
import at.tortmayr.chillisp.api.actions.SetModelAction;
import at.tortmayr.chillisp.api.actions.SetPopupModelAction;
import at.tortmayr.chillisp.api.actions.ToogleLayerAction;
import at.tortmayr.chillisp.api.actions.UpdateModelAction;
import io.typefox.sprotty.api.ILayoutEngine;
import io.typefox.sprotty.api.SModelElement;
import io.typefox.sprotty.api.SModelIndex;
import io.typefox.sprotty.api.SModelRoot;

public class RequestActionHandler implements IRequestActionHandler {

	private IGraphicalModelState modelState;
	private IGraphicalLanguageServer server;
	private IGraphicalModelSelectionListener selectionListener;
	private IGraphicalModelExpansionListener expansionListener;
	private IModelElementOpenListener modelElementOpenListener;
	private Object modelLock = new Object();
	private int revision = 0;
	private String lastSubmittedModelType;

	public RequestActionHandler(IGraphicalLanguageServer server) {
		this.server = server;
		this.modelState = server.getModelState();
		selectionListener = new IGraphicalModelSelectionListener.NullImpl();
		expansionListener = new IGraphicalModelExpansionListener.NullImpl();
		modelElementOpenListener = new IModelElementOpenListener.NullImpl();
	}

	protected void submitModel(SModelRoot newRoot, boolean update) {
		if (modelState.needsClientLayout()) {
			server.dispatch(new RequestBoundsAction(newRoot));
		} else {
			doSubmitModel(newRoot, update);
		}
		modelState.setCurrentModel(newRoot);
	}

	private void doSubmitModel(SModelRoot newRoot, boolean update) {

		ILayoutEngine layoutEngine = server.getLayoutEngine();
		if (layoutEngine != null) {
			layoutEngine.layout(newRoot);
		}

		synchronized (modelLock) {
			if (newRoot.getRevision() == revision) {
				String modelType = newRoot.getType();
				if (update && modelType != null && modelType.equals(lastSubmittedModelType)) {
					server.dispatch(new UpdateModelAction(newRoot, null, true));
				} else {
					server.dispatch(new SetModelAction(newRoot));
				}
				lastSubmittedModelType = modelType;
			}
		}
	}

	@Override
	public void handle(RequestModelAction action) {
		Map<String, String> options = action.getOptions();
		if (options != null) {
			modelState.setOptions(options);
			String needsClientLayout = options.get("needsClientLayout");
			if (needsClientLayout != null && !needsClientLayout.isEmpty()) {
				modelState.setNeedsClientLayout(Boolean.parseBoolean(needsClientLayout));
			}
			SModelRoot model = server.getModelFactory().loadModel(server);
			modelState.setCurrentModel(model);
			if (model != null) {
				submitModel(model, false);
			}
		}

	}

	@Override
	public void handle(CenterAction action) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handle(CollapseExpandAction action) {
		Set<String> expandedElements = modelState.getExpandedElements();
		if (action.getCollapseIds() != null) {
			expandedElements.removeAll(Arrays.asList(action.getCollapseIds()));
		}
		if (action.getExpandIds() != null) {
			expandedElements.addAll(Arrays.asList(action.getExpandIds()));
		}

		if (expansionListener != null) {
			expansionListener.expansionChanged(action, server);
		}

	}

	@Override
	public void handle(CollapseExpandAllAction action) {
		Set<String> expandedElements = modelState.getExpandedElements();
		expandedElements.clear();
		if (action.isExpand()) {
			new SModelIndex(modelState.getCurrentModel()).allIds().forEach(id -> expandedElements.add(id));
		}
		if (expansionListener != null) {
			expansionListener.expansionChanged(action, server);
		}

	}

	@Override
	public void handle(ComputedBoundsAction action) {
		synchronized (modelLock) {
			SModelRoot model = modelState.getCurrentModel();
			if (model != null && model.getRevision() == action.getRevision()) {
				LayoutUtil.applyBounds(model, action);
				doSubmitModel(model, true);
			}
		}

	}

	@Override
	public void handle(ExecuteNodeCreationToolAction action) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handle(ExecuteToolAction action) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handle(RequestBoundsChangeHintsAction action) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handle(ChangeBoundsAction action) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handle(RequestMoveHintsAction action) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handle(MoveAction action) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handle(FitToScreenAction action) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handle(OpenAction action) {
		if (modelElementOpenListener != null) {
			modelElementOpenListener.elementOpened(action, server);
		}
	}

	@Override
	public void handle(RequestExportSvgAction action) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handle(RequestLayersAction action) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handle(RequestPopupModelAction action) {
		SModelRoot model = modelState.getCurrentModel();
		SModelElement element = SModelIndex.find(model, action.getElementId());
		if (server.getPopupModelFactory() != null) {
			SModelRoot popupModel = server.getPopupModelFactory().createPopuModel(element, action, server);
			if (popupModel != null) {
				server.dispatch(new SetPopupModelAction(popupModel));
			}
		}

	}

	@Override
	public void handle(RequestToolsAction action) {

	}

	@Override
	public void handle(SelectAction action) {
		Set<String> selectedElements = modelState.getSelectedElements();
		if (action.getDeselectedElementsIDs() != null) {
			selectedElements.removeAll(Arrays.asList(action.getDeselectedElementsIDs()));
		}
		if (action.getSelectedElementsIDs() != null) {
			selectedElements.addAll(Arrays.asList(action.getSelectedElementsIDs()));
		}
		if (selectionListener != null) {
			selectionListener.selectionChanged(action, server);
		}

	}

	@Override
	public void handle(SelectAllAction action) {
		Set<String> selectedElements = modelState.getSelectedElements();
		if (action.isSelect()) {
			new SModelIndex(modelState.getCurrentModel()).allIds().forEach(id -> selectedElements.add(id));
		} else
			selectedElements.clear();
		if (selectionListener != null) {
			selectionListener.selectionChanged(action, server);
		}
	}

	@Override
	public void handle(ToogleLayerAction action) {
		// TODO Auto-generated method stub

	}
}
