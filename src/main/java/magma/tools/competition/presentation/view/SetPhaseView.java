/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package magma.tools.competition.presentation.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JFrame;

import magma.tools.competition.domain.ChangeHandler;
import magma.tools.competition.domain.ChangeNotifier;
import magma.tools.competition.domain.Phase;
import magma.tools.competition.domain.Tournament;
import magma.tools.competition.presentation.model.SetPhaseComboBoxModel;

import com.google.common.collect.Lists;

/**
 *
 * @author simon
 */
public class SetPhaseView extends javax.swing.JFrame implements ChangeNotifier<Phase>
{
	private static final long serialVersionUID = -4421752243384524130L;

	private Tournament tournament;

	private JFrame jFrame;

	private LinkedList<ChangeHandler<Phase>> handlers;

	public SetPhaseView(Tournament tournament)
	{
		this.tournament = tournament;
		handlers = Lists.newLinkedList();
		initComponents();
	}

	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents()
	{
		jFrame = this;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Set current phase");
		SetPhaseComboBoxModel comboBoxModel = new SetPhaseComboBoxModel(tournament);
		jLabel1 = new javax.swing.JLabel();
		jComboBox1 = new javax.swing.JComboBox(comboBoxModel);
		jButton1 = new javax.swing.JButton();
		jButton2 = new javax.swing.JButton();

		jLabel1.setText("Phase:");
		jButton1.setText("OK");
		jButton1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Phase newPhase = comboBoxModel.getNewPhase();
				if (newPhase != null) {
					for (ChangeHandler<Phase> handler : handlers) {
						handler.onChange(newPhase);
					}
				}

				jFrame.setVisible(false);
			}
		});

		jButton2.setText("Cancle");
		jButton2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				jFrame.setVisible(false);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
										  .addContainerGap()
										  .addComponent(jLabel1)
										  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										  .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 282,
												  javax.swing.GroupLayout.PREFERRED_SIZE)
										  .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
								layout.createSequentialGroup()
										.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(jButton2)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jButton1)
										.addContainerGap()));
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout.createSequentialGroup()
										.addContainerGap()
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
														  .addComponent(jLabel1)
														  .addComponent(jComboBox1,
																  javax.swing.GroupLayout.PREFERRED_SIZE,
																  javax.swing.GroupLayout.DEFAULT_SIZE,
																  javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
														  .addComponent(jButton1)
														  .addComponent(jButton2))
										.addContainerGap()));

		pack();
	} // </editor-fold>//GEN-END:initComponents

	@Override
	public void addChangeHandler(ChangeHandler<Phase> handler)
	{
		handlers.add(handler);
	}

	@Override
	public void removeChangeHandler(ChangeHandler<Phase> handler)
	{
		handlers.add(handler);
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton jButton1;

	private javax.swing.JButton jButton2;

	private javax.swing.JComboBox jComboBox1;

	private javax.swing.JLabel jLabel1;
	// End of variables declaration//GEN-END:variables
}
