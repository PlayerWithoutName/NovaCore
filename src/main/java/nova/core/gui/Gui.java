package nova.core.gui;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import nova.core.entity.Entity;
import nova.core.game.Game;
import nova.core.gui.ComponentEvent.ComponentEventListener;
import nova.core.gui.ComponentEvent.SidedComponentEvent;
import nova.core.gui.GuiEvent.BindEvent;
import nova.core.gui.components.inventory.Slot;
import nova.core.gui.factory.GuiEventFactory;
import nova.core.gui.factory.GuiFactory;
import nova.core.gui.nativeimpl.NativeGui;
import nova.core.gui.render.text.TextMetrics;
import nova.core.inventory.Inventory;
import nova.core.loader.NovaMod;
import nova.core.network.NetworkTarget.Side;
import nova.core.network.Packet;
import nova.core.player.InventoryPlayer;
import nova.core.player.Player;
import nova.core.util.transform.Vector3i;

/**
 * Root container for GUI
 */
public class Gui extends AbstractGuiContainer<Gui, NativeGui> {

	protected Optional<String> modID = Optional.empty();
	protected final boolean hasServerSide;

	/**
	 * List of {@link Inventory Inventories} associated with this GUI.
	 */
	protected final HashMap<String, Inventory> inventoryMap = new HashMap<>();
	protected InventoryPlayer playerInventory;

	/**
	 * Creates a nwe GUI instance with a {@link Side#SERVER server} side
	 * backend. You can register events with
	 * {@link #onEvent(ComponentEventListener, Class, Side)} specifying on which
	 * side the event gets processed. Keep the client side restrictions in mind.
	 * 
	 * @param uniqueID Unique ID of this GUI
	 * @see #Gui(String, boolean)
	 */
	public Gui(String uniqueID) {
		super(uniqueID, NativeGui.class);
		this.hasServerSide = true;
	}

	/**
	 * Creates a new GUI instance with an optional {@link Side#SERVER server}
	 * side backend. GUIs without a server side aren't able to send events over
	 * the network, adding a listener for the server side won't have any effect.
	 * Keep the client side restrictions in mind.
	 * 
	 * @param uniqueID Unique ID of this GUI
	 * @param hasServerSide Optional server side backend
	 */
	public Gui(String uniqueID, boolean hasServerSide) {
		super(uniqueID, NativeGui.class);
		this.hasServerSide = hasServerSide;
	}

	public boolean hasServerSide() {
		return hasServerSide;
	}

	/**
	 * Initializes this GUI with a {@link NovaMod} mod id, you shouldn't be
	 * using this unless really necessary, in general it's done by the
	 * {@link GuiFactory}.
	 * 
	 * @param modID NOVA mod id
	 */
	public void setModID(String modID) {
		this.modID = Optional.of(modID);
	}

	/**
	 * Returns the {@link NovaMod} mod id referenced by this GUI. Populated once
	 * registered with a {@link GuiEventFactory} or in case of an unregistered
	 * GUI, after bind. Can be used for identifying purposes.
	 * 
	 * @return NOVA mod id
	 */
	public Optional<String> getModID() {
		return modID;
	}

	protected void dispatchNetworkEvent(SidedComponentEvent event, GuiComponent<?, ?> sender) {
		// Block outgoing packets from components without identifier. Has to
		// silently catch them.
		if (!sender.hasIdentifer())
			return;
		Packet packet = Game.instance.networkManager.newPacket();
		GuiEventFactory.instance.constructPacket(event, this, packet, event.getSyncID());
		getNative().dispatchNetworkEvent(packet);
	}

	/**
	 * Binds the GUI, called when displayed.
	 * 
	 * @param entity Entity which interacted to display this GUI
	 * @param position block position
	 */
	public void bind(Entity entity, Vector3i position) {
		inventoryMap.clear();
		playerInventory = ((Player) entity).getInventory();
		onEvent(new GuiEvent.BindEvent(this, entity, position));
		repaint();
	}

	/**
	 * Unbind the GUI, called after getting replaced by another GUI.
	 */
	public void unbind() {
		inventoryMap.clear();
		playerInventory = null;
		onEvent(new GuiEvent.UnBindEvent(this));
	}

	public TextMetrics getTextMetrics() {
		return getNative().getTextMetrics();
	}

	@Override
	public Optional<Gui> getParentGui() {
		// TODO Parented GUIs?
		return Optional.of(this);
	}

	/**
	 * Associates an {@link Inventory} with this GUI. Has to be called from the
	 * {@link BindEvent} in order to supply it to {@link Slot}.
	 * 
	 * @param id Id used to indentify the inventory
	 * @param inventory inventory to bind
	 */
	public void addInventory(String id, Inventory inventory) {
		Objects.requireNonNull(inventory);
		inventoryMap.put(id, inventory);
	}

	public Inventory getInventory(String id) {
		return inventoryMap.get(id);
	}

	public InventoryPlayer getPlayerInventory() {
		return playerInventory;
	}
}
