package scratch;

import com.intellij.openapi.util.Condition;
import com.intellij.util.containers.ContainerUtil;
import scratch.filesystem.FileSystem;
import scratch.ide.Ide;

import java.util.List;

import static com.intellij.util.containers.ContainerUtil.newArrayList;

/**
* User: dima
* Date: 10/02/2013
*/
public class Scratch {
	private final Ide ide;
	private final FileSystem fileSystem;
	private ScratchConfig config;

	public Scratch(Ide ide, FileSystem fileSystem, ScratchConfig config) {
		this.ide = ide;
		this.fileSystem = fileSystem;
		this.config = config;
	}

	public void migrate(List<String> scratchTexts) {
		List<Integer> indexes = newArrayList();
		List<ScratchInfo> scratchesInfo = newArrayList();

		for (int i = 1; i <= scratchTexts.size(); i++) {
			String scratchName = (i == 1 ? "&scratch" : "scratch&" + i);
			ScratchInfo scratchInfo = new ScratchInfo(scratchName, "txt");

			boolean wasCreated = fileSystem.createFile(scratchInfo.asFileName(), scratchTexts.get(i - 1));
			if (wasCreated) {
				scratchesInfo.add(scratchInfo);
			} else {
				indexes.add(i);
			}
		}

		if (indexes.isEmpty()) {
			ide.migratedScratchesToFiles();
		} else {
			ide.failedToMigrateScratchesToFiles(indexes);
		}
		config = config.with(scratchesInfo).needsMigration(false);
		ide.updateConfig(config);
	}

	public void userWantsToSeeScratchesList() {
		List<String> fileNames = fileSystem.listOfScratchFiles();

		List<ScratchInfo> scratchesInfo = newArrayList();
		for (String fileName : fileNames) {
			scratchesInfo.add(ScratchInfo.createFrom(fileName));
		}
		ide.displayScratchesListPopup(scratchesInfo);
	}

	public void userWantsToOpenScratch(ScratchInfo scratchInfo) {
		if (fileSystem.fileExists(scratchInfo.asFileName()))
			ide.openScratch(scratchInfo);
		else
			ide.failedToOpen(scratchInfo);
	}

	public void userWantsToOpenDefaultScratch() {
		if (config.scratchInfos.isEmpty()) {
			ide.failedToOpenDefaultScratch();
		} else {
			ScratchInfo scratchInfo = config.scratchInfos.get(0);
			if (fileSystem.fileExists(scratchInfo.asFileName())) {
				ide.openScratch(scratchInfo);
			} else {
				ide.failedToOpenDefaultScratch();
			}
		}
	}

	public boolean canUserRename(final ScratchInfo scratchInfo, String fullNameWithMnemonics) {
		final ScratchInfo renamedScratchInfo = ScratchInfo.createFrom(fullNameWithMnemonics);

		return !ContainerUtil.exists(config.scratchInfos, new Condition<ScratchInfo>() {
			@Override public boolean value(ScratchInfo it) {
				return !it.equals(scratchInfo) && it.name.equals(renamedScratchInfo.name);
			}
		});
	}

	public void userWantsToRename(ScratchInfo scratchInfo, String fullNameWithMnemonics) {
		ScratchInfo renamedScratchInfo = ScratchInfo.createFrom(fullNameWithMnemonics);

		boolean wasRenamed = fileSystem.renameFile(scratchInfo.asFileName(), renamedScratchInfo.asFileName());
		if (wasRenamed) {
			config = config.replace(scratchInfo, renamedScratchInfo);
			ide.updateConfig(config);
		} else {
			ide.failedToRename(scratchInfo);
		}
	}

	public void userMovedScratch(final ScratchInfo scratchInfo, int shift) {
		config = config.move(scratchInfo, shift);
		ide.updateConfig(config);
	}

	public void userWantsToListenToClipboard(boolean value) {
		ide.updateConfig(config.listenToClipboard(value));
	}
}