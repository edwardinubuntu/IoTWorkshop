package tw.soleil.indoorlocation.util;

import android.util.Log;

import com.lemmingapex.trilateration.LinearLeastSquaresSolver;
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import java.util.ArrayList;
import java.util.List;

import tw.soleil.indoorlocation.InDoorDemo;
import tw.soleil.indoorlocation.object.IndoorObject;

/**
 * Created by edward_chiang on 7/13/16.
 */
public class LocationCalculator {

    private List<IndoorObject> positions;

    public LocationCalculator() {
        positions = new ArrayList<>();
    }

    public List<IndoorObject> getPositions() {
        return positions;
    }

    public void setPositions(List<IndoorObject> positions) {
        this.positions = positions;
    }

    public double[] calculateCentroid() {

        double[][] indoorPositions = new double[positions.size()][2];
        double[] distance = new double[positions.size()];

        for (int r=0; r<indoorPositions.length; r++) {
            indoorPositions[r] = positions.get(r).getPosition();
            distance[r] = positions.get(r).getRelativeDistance();
        }

        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(indoorPositions, distance), new LevenbergMarquardtOptimizer());
        LeastSquaresOptimizer.Optimum optimum = solver.solve();

        return optimum.getPoint().toArray();
    }
}
