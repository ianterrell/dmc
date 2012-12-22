<?php include("header.html"); ?>

<h2 class="subheadline">user guide</h2>

<p>The visualization program is fairly straightforward and easy to use.  If you are confused about the purpose of an item, try hovering your mouse pointer on top of the item or related items; all editable user interface elements have descriptive tool tips that will pop up.</p>
<p>That said, here is a description of the various elements of the GUI.</p>

<div class="menu">
<a href="#navigation">Navigation</a> |
<a href="#controls">Controls</a> |
<a href="#inputs">Input Parameters</a> |
<a href="#potentials">Potentials</a> |
<a href="#graphs">Graphs</a> |
<a href="#display">Display Options</a>
</div>

<a name="navigation"></a>
<h3>Navigation</h3>
<table>
<tr>
<td class="c"><img src="images/tabs.jpg"></td>
<td>
<p>The simulation has two main screens, each accessed by a tab at the top left of the window.</p>

<p>Clicking on "Graphs" will bring you to the screen with the graphs and the control buttons, while clicking on "Settings" will bring you to the screen full of settings options.</p>
</td>
</tr>
</table>
<div class="totop"><a href="#top">Back to Top</a></div>

<a name="controls"></a>
<h3>Controls</h3>
<table>
<tr>
<td class="c"><img src="images/control.jpg"></td>
<td>
<p>These buttons control the simulation.  Like their names indicate, they start a new simulation, pause a running simulation, continue a paused simulation, and restart a paused simulation.</p>
</td>
</tr>
<tr>
<td class="c"><img src="images/defaults.jpg"></td>
<td><p>This button, found under the "Settings" tab, resets all of the settings to their default values.</p></td>
</tr>
</table>
<div class="totop"><a href="#top">Back to Top</a></div>

<a name="inputs"></a>
<h3>Input Parameters</h3>
<table>
<tr>
<td class="c"><img src="images/inputparam.jpg"></td>
<td><p>Use these text fields to input the following variables:
<ul>
<li><b>Num. Walkers</b> - The initial number of walkers, and the desired number to maintain throughout the simulation.
<li><b>Warmup It.</b> - The number of iterations to use as a warmup period, during which no data will be collected for the estimate of the ground state wavefunction.
<li><b>dTau</b> - The timestep to use.
<li><b>Alpha</b> - The feedback parameter to use in recalculating the reference energy (see Eq. (12) in the thesis).  Use a negative value to use the simulation default of 1/dTau.
<li><b>Seed</b> - The number to use to seed the random variate generator.
</ul></p>
</td>
</tr>
<tr>
<td class="c"><img src="images/walkerinit.jpg"></td>
<td><p>Use the combo box to select how the walkers will be initialized.  The options are:
<ul>
<li><b>Delta Function</b> - Set all of the walkers' positions to the point given by the editable parameter x_0.
<li><b>Uniform Distribution</b> - Uniformly distribute the walkers' positions between the editable parameters a and b.
<li><b>Gaussian Distribution</b> - Distribute the walkers' positions according to the Gaussian distribution with a mean of mu and a standard deviation of sigma, both editable.
</ul></p>
</td>
</tr>
<tr>
<td class="c"><img src="images/refenergy.jpg"></td>
<td><p>Use this text field to enter the desired initial reference energy, and if it should be held constant.  Use a negative number to initialize the reference energy with the average potential of the initialized walkers.</p></td>
</tr>
</table>
<div class="totop"><a href="#top">Back to Top</a></div>

<a name="potentials"></a>
<h3>Potentials</h3>
<table>
<tr>
<td class="c"><img src="images/potential.jpg"></td>
<td><p>Use the combo box to select which potential to simulate (at the moment only the simple harmonic oscillator potential is implemented).  Use the checkboxes and color buttons to select whether or not the theoretical values should be graphed on the appropriate graphs, and if so, what colors they should be.</p>
</td>
</tr>
</table>
<div class="totop"><a href="#top">Back to Top</a></div>

<a name="graphs"></a>
<h3>Graphs</h3>
<table>
<tr>
<td class="c"><img src="images/options_graphselections.jpg"></td>
<td><p>There are five different types of graphs currently implemented.  Use these combo boxes, found under the graphs themselves under the "Graphs" tab, to select where each type of graph should be displayed, if at all.</p>
</td>
</tr>
</table>
<table>
<tr>
<td class="c"><img src="images/graphoptions.jpg"></td>
<td><p>Each type of graph has a set of options that can be customized.  Use this panel, found under the "Settings" tab, to customize them.  The specific options for each type of graph are listed below, along with a picture of the graph.</p>
</td>
</tr>
</table>
<table>
<tr>
<td><img src="images/histogramgraph.jpg"></td>
<td><p></p><p>The histogram graph shows the current distribution of the walkers.  The number of bins the histogram contains is editable with the slider found under the "Graphs" tab, while the graph options under the "Settings" tab allow the user to change the minimum and maximum x value for the histogram, whether or not the area of the histogram should be normalized to unity, the color of the histogram, and whether the data should be displayed as lines or points.</p>
</td>
</tr>
<tr>
<td><img src="images/numwalkersgraph.jpg"></td>
<td><p>This graph displays the number of walkers that currently exist in the simulation.  The data can be displayed as either discrete points or connected lines, in a color of the users choice.  The user can also set the maximum number of points to display, the new points replacing the oldest points in the data set graphed.</p>
</td>
</tr>
<tr>
<td><img src="images/refenergygraph.jpg"></td>
<td><p>This graph shows the current reference energy of the simulation, and the editable values are the same as that of the number of walkers graph.  The theoretical energy can be graphed on this graph by checking the appropriate box under "Potential."</p>
</td>
</tr>
<tr>
<td><img src="images/e0estimategraph.jpg"></td>
<td><p>This graph shows the average of successive reference energies, which becomes an estimate of the ground state energy.  The data is displayable as either lines or points, in a user chosen color.  Since the energy converges fairly rapidly to the theoretical value, the user can choose to cut off the data collection after a certain number of iterations.  The theoretical energy can be graphed on this graph by checking the appropriate box under "Potential."</p>
</td>
</tr>
<tr>
<td><img src="images/phi0estimategraph.jpg"></td>
<td><p>This graph shows the estimate of the ground state wavefunction.  Since most of this is hard coded, the only editable option is the color of the data.  The theoretical ground state wavefunction is graphed on this graph, if chosen in the "Potential" field.</p>
</td>
</tr>
</table>
<div class="totop"><a href="#top">Back to Top</a></div>

<a name="display"></a>
<h3>Display Options</h3>
<table>
<tr>
<td class="c"><img src="images/displayoptions.jpg"></td>
<td><p>The simulation runs the user editable number of iterations every user editable number of milliseconds (approximately).  After each cycle, the graphs are updated.</p>
</td>
</tr>
</table>
<div class="totop"><a href="#top">Back to Top</a></div>

<?php include("footer.html"); ?>