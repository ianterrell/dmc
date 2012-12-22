<?php include("header.html"); ?>

<h2 class="subheadline">homepage</h2>

<p>This is the homepage of dmcView, the senior project of Ian Terrell for <a href="http://web.wm.edu/physics/">physics</a> at <a href="http://www.wm.edu">The College of William and Mary</a>.  It's a Diffusion Monte Carlo simulation of a single particle in a one dimensional simple harmonic oscillator, complete with a graphical user interface.  Original code and the website design are &copy; 2004 Ian Terrell.  Please use the above menu to navigate.</p>

<h3>Credits</h3>

<p>The simulation algorithm implemented is adapted from the paper "Introduction to the Diffusion Monte Carlo Method" by Ioan Kosztin, Byron Faber and Klaus Schulten, published in the 5th issue of the 64th volume of the <i>American Journal of Physics</i> in May of 1996, found on pages 633-644.</p>

<p>The visualization makes use of the <a href="http://www.math.uah.edu/psol/objects/edu/uah/math/devices/Graph.html">Graph</a> package developed by the University of Alabama in Huntsville.</p>

<p>The random variate generation algorithms come from C code written by Steve Park and Dave Geyer for an introductory simulation class at William and Mary.</p>

<h3>Frequently Asked Questions</h3>

<p><b>Q</b>: I can't see the applet, what's wrong?
<br><b>A</b>: Make sure your browser has the latest <a href="http://java.sun.com/downloads/index.html">Java plugin</a> installed.</p>

<p><b>Q</b>: I use Mac OS X, and the applet looks funny.
<br><b>A</b>: The applet is probably simulating your native Aqua look-and-feel, which has slightly larger GUI elements.  Add code to use Java's standard metal look-and-feel, or change the sizes of the GUI container elements to be big enough.</p>

<p><b>Q</b>: I can't edit the input parameters, what's wrong?
<br><b>A</b>: The input parameters aren't editable after a simulation has started; to start a new simulation with new input parameters, first reset the simulation.</p>

<?php include("footer.html"); ?>