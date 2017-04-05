package magma.tools.competition.presentation.model;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;

import magma.tools.competition.domain.ChangeHandler;
import magma.tools.competition.domain.ChangeNotifier;
import magma.tools.competition.domain.Tournament;
import magma.tools.competition.json.JsonHandler;
import magma.tools.competition.util.ClusterConfiguration;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.Lists;

public class OpenTournamentMouseAdapter extends MouseAdapter implements ChangeNotifier<Tournament>
{
	private List<ChangeHandler<Tournament>> handlers;

	private JFileChooser chooser;

	private Tournament tournament;

	public OpenTournamentMouseAdapter()
	{
		this.chooser = new JFileChooser("Choose Directory");
		handlers = Lists.newLinkedList();
	}

	@Override
	public void mousePressed(MouseEvent arg0)
	{
		try {
			// TODO: change ClusterConfiguration to not throw IOExceptions
			String path = ClusterConfiguration.get().getFileStartPath();
			final File file = new File(path);

			chooser.setDialogType(JFileChooser.OPEN_DIALOG);
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setCurrentDirectory(file);
			chooser.setVisible(true);
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		final int result = chooser.showOpenDialog(null);

		if (result == JFileChooser.APPROVE_OPTION) {
			File inputVerzFile = chooser.getSelectedFile();
			String inputVerzStr = inputVerzFile.getPath();
			try {
				File jsonFile = new File(inputVerzStr);
				tournament = JsonHandler.createFromFile(jsonFile, Tournament.class);

			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (JsonParseException e1) {
				e1.printStackTrace();
			} catch (JsonMappingException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		} else {
			chooser.setSelectedFile(null);
		}

		chooser.setVisible(false);
		for (ChangeHandler<Tournament> handler : handlers) {
			handler.onChange(tournament);
		}
	}

	public Tournament getTournament()
	{
		return tournament;
	}

	@Override
	public void addChangeHandler(ChangeHandler<Tournament> handler)
	{
		handlers.add(handler);
	}

	@Override
	public void removeChangeHandler(ChangeHandler<Tournament> handler)
	{
		handlers.remove(handler);
	}
}
