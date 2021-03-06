package nova.core.block.components;

import nova.core.block.Block;
import nova.core.render.model.BlockModelUtil;
import nova.core.render.model.Model;

/**
 * This interface specifies that a block requires custom static rendering.
 * This type of rendering only updates its render state every time the world changes (block change)
 */
public interface StaticRenderer {

	/**
	 * Called when this block is to be rendered.
	 * @param model A {@link nova.core.render.model.Model} to use.
	 */
	default void renderStatic(Model model) {
		BlockModelUtil.drawBlock(model, (Block) this);
	}
}
