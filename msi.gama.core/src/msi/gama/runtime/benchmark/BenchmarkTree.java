package msi.gama.runtime.benchmark;

import msi.gama.common.interfaces.IBenchmarkable;
import msi.gama.util.tree.GamaNode;
import msi.gama.util.tree.GamaTree;
import msi.gaml.descriptions.ExperimentDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IOperator;

public class BenchmarkTree extends GamaTree<IRecord> {

	public BenchmarkTree(final IDescription model, final ExperimentDescription focusedExperiment) {
		setRoot(createNode(model, 0));
		final GamaNode<IRecord> currentNode = getRoot();
		final IDescription currentDescription = model;
		feedTreeFrom(currentDescription, focusedExperiment, currentNode, 1);
	}

	GamaNode<IRecord> createNode(final IBenchmarkable d, final int level) {
		return new GamaNode<>(new BenchmarkRecord(d), level);
	}

	private void feedTreeFrom(final IDescription currentDescription, final ExperimentDescription focusedExperiment,
			final GamaNode<IRecord> currentNode, final int level) {
		currentDescription.visitFacets((name, exp) -> {
			final IExpression expr = exp.getExpression();
			if (expr instanceof IOperator) {
				final IOperator op = (IOperator) expr;
				final GamaNode<IRecord> currentOp = currentNode.addChild(createNode(op, level));
				feedTreeFrom(op, currentOp, level + 1);
			}
			return true;
		});
		currentDescription.visitOwnChildren((d) -> {
			if (d instanceof ExperimentDescription && !d.equals(focusedExperiment)) { return true; }
			final GamaNode<IRecord> node = currentNode.addChild(createNode(d, level));
			feedTreeFrom(d, focusedExperiment, node, level + 1);
			return true;
		});
	}

	private void feedTreeFrom(final IOperator op, final GamaNode<IRecord> currentNode, final int level) {
		op.visitSuboperators((o) -> {
			final GamaNode<IRecord> node = currentNode.addChild(createNode(o, level));
			feedTreeFrom(o, node, level + 1);
		});

	}

}
