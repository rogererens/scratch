package scratch.ide;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.openapi.util.text.StringUtil;
import scratch.ScratchConfig;
import scratch.ScratchInfo;

import javax.swing.*;
import java.util.List;

import static com.intellij.openapi.ui.popup.JBPopupFactory.ActionSelectionAid.NUMBERING;

/**
* User: dima
* Date: 10/02/2013
*/
public class Ide {
	private static final Logger LOG = Logger.getInstance(Ide.class);

	public void migratedScratchesToFiles() {
		LOG.info("Migrated scratches to physical files");
	}

	public void failedToMigrateScratchesToFiles(List<Integer> scratchIndexes) {
		LOG.error("Failed to migrated scratches to physical files. " +
				"Failed scratches: " + StringUtil.join(scratchIndexes, ", "));
	}

	public void updateConfig(ScratchConfig config) {
		ScratchConfigPersistence.getInstance().updateFrom(config);
	}

	public void displayScratchesListPopup(List<ScratchInfo> scratchInfos, UserDataHolder userDataHolder) {
		AnActionEvent event = userDataHolder.getUserData(ScratchComponent.ACTION_EVENT_KEY);
		Project project = event.getProject();
		DefaultActionGroup actionGroup = createActionGroup(scratchInfos, project);

		JBPopupFactory factory = JBPopupFactory.getInstance();
		ListPopup listPopup = factory.createActionGroupPopup("List of Scratches", actionGroup, event.getDataContext(), NUMBERING, true);
		listPopup.showCenteredInCurrentWindow(project);
	}

	public void openScratch(ScratchInfo scratchInfo) {
		// TODO implement

	}

	public void failedToOpen(ScratchInfo scratchInfo) {
		// TODO implement

	}

	public void failedToOpenDefaultScratch() {
		// TODO implement

	}

	public void failedToRename(ScratchInfo scratchInfo) {
		// TODO implement

	}

	private DefaultActionGroup createActionGroup(List<ScratchInfo> scratchInfos, final Project project) {
		DefaultActionGroup actionGroup = new DefaultActionGroup();
		for (final ScratchInfo scratchInfo : scratchInfos) {
			String name = scratchInfo.fullNameWithMnemonics();
			actionGroup.add(new AnAction(name, "Open " + scratchInfo.name, getIcon(name)) {
				@Override public void actionPerformed(AnActionEvent event) {
					ScratchComponent.instance().userWantsToOpenScratch(scratchInfo);
				}
			});
		}
		return actionGroup;
	}

	private static Icon getIcon(String scratchName) {
		String[] split = scratchName.split("\\.");
		if (split.length > 1) {
			String fileType = split[split.length - 1];
			return IconLoader.findIcon("/fileTypes/" + fileType + ".png"); // TODO use FileType class
		} else {
			return AllIcons.FileTypes.Text;
		}
	}
}
