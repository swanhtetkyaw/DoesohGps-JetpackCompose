package seeker.doesoh.tracker.util

import seeker.doesoh.tracker.data.model.Fuel
import seeker.doesoh.tracker.data.remote.Position

object FuelUtil {
    private const val TAG = "Fuel Utilities"

    fun filterFuelData(positions: List<Position>): ArrayList<Fuel> {
        var previousPosition: Position? = null
        val filteredFuel = ArrayList<Fuel>()
        if(positions.isNotEmpty()) {
            positions.forEachIndexed() { index,position ->
                //Second Iteration
                previousPosition?.let { previousPosition ->
                    if(position.attributes.containsKey("fuel") && previousPosition.attributes.containsKey("fuel")) {
                        val currentFuelLevel = position.attributes["fuel"] as Double
                        val previousFuelLevel = previousPosition.attributes["fuel"] as Double
                        val isMoving = position.speed > 5
                        if(currentFuelLevel != previousFuelLevel) {
                            //isMoving and currentFuelLevel is higher,consider as Jump points
                            if(isMoving && currentFuelLevel > previousFuelLevel) return@forEachIndexed
                            //is not moving and the fuel drop less than 5, consider as sensor unstable drop points
                            if(!isMoving && previousFuelLevel > currentFuelLevel && previousFuelLevel - currentFuelLevel < 5) return@forEachIndexed
                            val address = position.address ?: "${position.latitude} : ${position.longitude}"
                            val date = Utilities.getUserDateFormat(true,position.fixTime)
                            filteredFuel.add(Fuel(currentFuelLevel.toInt(),date,address))
                        }
                    }
                }
                previousPosition = position
            }
        }

        return filteredFuel
    }

    fun fuelSpent(filteredFuel: List<Fuel>,filledFuel: Int) {
        if(filteredFuel.isNotEmpty()) {

        }
    }

    fun fuelConsumption(fuelSpent: Int,totalDistance: Int) {

    }

    fun getTotalDistanceOdometer(firstPosition: Position,lastPosition: Position): Int {
        var totalDistance = 0
        if(firstPosition.attributes.containsKey("odometer") && lastPosition.attributes.containsKey("odometer")) {
            val firstOdometer = firstPosition.attributes["odometer"] as Int
            val lastOdometer = lastPosition.attributes["odometer"] as Int
            totalDistance = lastOdometer - firstOdometer
        }

        return totalDistance
    }

}