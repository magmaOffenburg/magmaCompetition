package magma.tools.competition;

import java.awt.EventQueue;
import java.io.IOException;

import javax.swing.JOptionPane;

import magma.tools.competition.csv.CsvReaderModule;
import magma.tools.competition.domain.DomainModule;
import magma.tools.competition.domain.TournamentBuilder;
import magma.tools.competition.json.JsonHandlerModule;
import magma.tools.competition.presentation.view.MainFrame;
import magma.tools.competition.util.ClusterConfiguration;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

public class SimManager
{
	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		Injector injector = Guice.createInjector(new DomainModule(),
				new CsvReaderModule(), new JsonHandlerModule());

		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
					.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		}

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				try {
					Provider<TournamentBuilder> tournamentBuilder = injector
							.getInstance(Key
									.get(new TypeLiteral<Provider<TournamentBuilder>>() {
									}));
					if (verifyClusterConfiguration()) {
						MainFrame frame = new MainFrame(tournamentBuilder);
						frame.setVisible(true);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static boolean verifyClusterConfiguration()
	{
		try {
			ClusterConfiguration configuration = ClusterConfiguration.get();

			String result = configuration.checkValid();
			if (result.length() > 0) {
				JOptionPane.showMessageDialog(null,
						"Invalid cluster configuration. Please check the following settings:\n\n"
								+ result);
				return false;
			}
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
					"Cluster configuration missing or unreadable.");
			return false;
		}
	}

}
