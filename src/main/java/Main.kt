import kotlin.math.abs

const val invalidResponse = "Invalid response.\n"
const val chooseTarget = "\nChoose the Target cell (e.g 2D)"
const val initialText = "\nWelcome to DEISI Minesweeper\n\n1 - Start New Game\n0 - Exit Game\n"

fun startGame(): Boolean{
    println("\nWelcome to DEISI Minesweeper\n\n1 - Start New Game\n0 - Exit Game\n")
    var start = readLine()?.toIntOrNull() ?: 2
    while(start != 1 && start != 0){
        println(invalidResponse)
        println(initialText)
        start = readLine()?.toIntOrNull() ?: 2
    }
    return start == 1
}

fun isNameValid(name: String?, minLength: Int = 3): Boolean{
    return name != null && name.length >= minLength
}

fun showLegend(legend: String): Boolean{
    var varLegend = legend
    while (varLegend != "Y" && varLegend != "N"){
        println(invalidResponse)
        println("Show legend (y/n)?")
        varLegend = readLine().toString()
        varLegend = varLegend.uppercase()
    }
    if(varLegend == "Y"){
        return true
    }
    return false
}

fun createLegend(numColumns: Int): String{
    var legend = "A   "
    var i = 1
    while (legend.last() != 'A' + (numColumns - 1)){
        if(legend[(i * 4) - 4] == 'A' + (numColumns - 2)) {
            legend += ('A' + i)
        }
        else {
            legend = legend + ('A' + i) + "   "
            i++
        }
    }
    return legend
}

fun isValidGameMinesConfiguration(numLines: Int, numColumns: Int, numMines: Int): Boolean{
    if(numMines <= 0 || (numLines * numColumns - 2) < numMines) {
        return false
    }
    return true
}

fun calculateNumMinesForGameConfiguration(numLines: Int, numColumns: Int): Int?{
    when(numLines * numColumns - 2){
        in 14..20 -> return 6
        in 21..40 -> return 9
        in 41..60 -> return 12
        in 61..79 -> return 19
    }
    return null
}

fun numLines(lines: Int?): Int{
    var varLines = lines
    while (varLines == null || varLines < 4 || varLines > 9){
        println(invalidResponse)
        println("How many lines?")
        varLines = readLine()?.toIntOrNull()
    }
    return varLines
}

fun numColumns(columns: Int?): Int{
    var varColumns = columns
    while (varColumns == null || varColumns < 4 || varColumns > 9){
        println(invalidResponse)
        println("How many columns?")
        varColumns = readLine()?.toIntOrNull()
    }
    return varColumns
}

fun getSquareAroundPoint(line: Int, column: Int, numLines: Int, numColumns: Int): Pair<Pair<Int, Int>, Pair<Int, Int>> {
    var yLeft = line - 1
    var yRight = line + 1
    var xLeft = column - 1
    var xRight = column + 1
    when(column){
        0 -> xLeft = 0
        numColumns-1 -> xRight = numColumns-1
    }
    when(line){
        0 -> yLeft = 0
        numLines-1 -> yRight = numLines-1
    }

    return Pair(Pair(yLeft,xLeft),Pair(yRight,xRight))
}

fun getCoordinates (readText: String?): Pair<Int, Int>?{
    if (readText.isNullOrEmpty()){
        return null
    }
    var coordLine = readText.first()
    val coordColumn = readText.last()

    if(readText == "exit") {
        return Pair(-1, -1)
    }
    if(readText == "abracadabra") {
        return Pair(-2, -2)
    }
    if(readText.isNullOrEmpty() ||  !(coordLine.isDigit()) || !(coordColumn.isLetter()) || readText.length != 2){
        return null
    }
    coordLine -= 1
    val coordLineInt = coordLine.toInt() - '0'.toInt()
    val coordColumnInt: Int
    if(coordColumn.isUpperCase()) {
        coordColumnInt = coordColumn - 'A'
    }
    else{
        coordColumnInt = coordColumn - 'a'
    }
    return Pair(coordLineInt,coordColumnInt)
}

fun coordValidation(matrix: Array<Array<Pair<String, Boolean>>>, showLegend: Boolean, coord: Pair<Int, Int>?): Pair<Int, Int>{
    var coordVar = coord
    var coordString: String?
    while (coord == null){
        println(invalidResponse)
        println(makeTerrain(matrix, showLegend))
        println(chooseTarget)
        coordString = readLine()
        coordVar = getCoordinates(coordString)
    }
    return coordVar ?: Pair(0,0)
}

fun isCoordinateInsideTerrain(coord: Pair<Int, Int>, numColumns: Int, numLines: Int): Boolean{
    if(coord.first >= numLines || coord.second >= numColumns || coord.first < 0 || coord.second < 0){
        return false
    }
    return true
}

fun isMovementPValid(currentCoord : Pair<Int, Int>, targetCoord : Pair<Int, Int>): Boolean{
    val distanceX = abs(currentCoord.first - targetCoord.first)
    val distanceY = abs(currentCoord.second - targetCoord.second)
    if(distanceX > 1 || distanceY > 1 ){
        return false
    }
    return true
}

fun movementValidation(sideMove: Boolean, insideTerrain: Boolean): Boolean{
    if (!sideMove || !insideTerrain){
        return false
    }
    return true
}

fun isEmptyAround(matrixTerrain: Array<Array<Pair<String, Boolean>>>, centerY: Int, centerX: Int, yl: Int, xl: Int, yr: Int, xr: Int): Boolean{
    for(line in yl..yr){
        for (column in xl..xr){
            if ((matrixTerrain[line][column] == Pair("*", false) 
                || matrixTerrain[line][column] == Pair("*", true)
                || matrixTerrain[line][column] == Pair("P", true) && (matrixTerrain[centerY][centerX] != Pair("P", true))
                || matrixTerrain[line][column] == Pair("f", true))
                && matrixTerrain[line][column] != matrixTerrain[centerY][centerX]) {

                return false
            }
        }
    }
    return true
}

fun revealMatrix(matrixTerrain: Array<Array<Pair<String, Boolean>>>, coordY: Int, coordX: Int, endGame: Boolean = false){
    val numLines = getNumLines(matrixTerrain)
    val numColumns = getNumColumns(matrixTerrain)
    val square = getSquareAroundPoint(coordY, coordX, numLines, numColumns)

    for(line in square.first.first..square.second.first){
        for (column in square.first.second..square.second.second){
            val mines = countNumberOfMinesCloseToCurrentCell(matrixTerrain,coordY,coordX)
            if (endGame) {
                matrixTerrain[line][column] = Pair(" ", true)
            }
            if(matrixTerrain[line][column].first == " " && !endGame){
                matrixTerrain[line][column] = Pair(" ", true)
            }
            if(matrixTerrain[line][column].first == "$mines" && !endGame){
                matrixTerrain[line][column] = Pair("$mines", true)
            }
        }
    }
}

fun fillNumberOfMines(matrixTerrain: Array<Array<Pair<String, Boolean>>>){
    for(line in 0 until matrixTerrain.size){
        for (column in 0 until matrixTerrain[line].size){
            val mines = countNumberOfMinesCloseToCurrentCell(matrixTerrain, line, column)
            if(mines > 0 && (matrixTerrain[line][column] == Pair(" ", true) || matrixTerrain[line][column] == Pair(" ", false))){
                matrixTerrain[line][column] = Pair("$mines", false)
            }
        }
    }
}

fun getNumLines(matrixTerrain: Array<Array<Pair<String, Boolean>>>): Int{
    return matrixTerrain.size;
}

fun getNumColumns(matrixTerrain: Array<Array<Pair<String, Boolean>>>): Int{
    return matrixTerrain[0].size
}

fun countNumberOfMinesCloseToCurrentCell(matrixTerrain: Array<Array<Pair<String, Boolean>>>, centerY: Int, centerX: Int): Int{
    var yLeft = centerY - 1
    var yRight = centerY + 1
    var xLeft = centerX - 1
    var xRight = centerX + 1
    var mines = 0
    val numLines = getNumLines(matrixTerrain)
    val numColumns = getNumColumns(matrixTerrain)

    when(centerX){
        0 -> xLeft = 0
        numColumns-1 -> xRight = numColumns-1
    }
    when(centerY){
        0 -> yLeft = 0
        numLines-1 -> yRight = numLines-1
    }
    for(line in yLeft..yRight){
        for (column in xLeft..xRight){
            if (matrixTerrain[line][column] == Pair("*", false) || matrixTerrain[line][column] == Pair("*", true)) {
                mines++
            }
        }
    }
    return mines
}

fun createMatrixTerrain(numLines: Int, numColumns: Int, numMines: Int, ensurePathToWin: Boolean = false): Array<Array<Pair<String, Boolean>>>{
    val terrain = Array(numLines) { Array(numColumns) {Pair(" ", false)} }
    var i = 0
    terrain[0][0] = Pair("P",true)
    terrain[numLines-1][numColumns-1] = Pair("f",true)
    if(!ensurePathToWin){
        while(i < numMines) {
            val randomLines = (0 until numLines).random()
            val randomColumns = (0 until numColumns).random()
            if(terrain[randomLines][randomColumns] != Pair("*",false) && (randomColumns != 0 || randomLines != 0) && (randomLines != numLines-1 || randomColumns != numColumns-1)){
                i++
                terrain[randomLines][randomColumns] = Pair("*",false)
            }
        }
    }
    else{
        while(i < numMines) {
            val randomLines = (0 until numLines).random()
            val randomColumns = (0 until numColumns).random()
            val square = getSquareAroundPoint(randomLines, randomColumns, numLines, numColumns)

            if(terrain[randomLines][randomColumns] != Pair("*",false)
                && (randomColumns != 0 || randomLines != 0)
                && (randomLines != numLines-1 || randomColumns != numColumns-1)
                && isEmptyAround(terrain, randomLines, randomColumns, square.first.first, square.first.second, square.second.first, square.second.second)){

                i++
                terrain[randomLines][randomColumns] = Pair("*",false)
            }
        }
    }
    return terrain
}

fun makeTerrainLegendEverything(matrixTerrain: Array<Array<Pair<String, Boolean>>>): String{
    var i = 0
    var lineCount = 1
    var board = "    ${createLegend(matrixTerrain[0].size)}    \n 1 "
    for(line in 0 until matrixTerrain.size){
        for (column in 0 until matrixTerrain[line].size){
            board += " ${(matrixTerrain[line][column]).first} "
            if (column < matrixTerrain[line].size-1){
                board += "|"
            }
        }
        if (i < matrixTerrain.size-1) {
            board += "\n   "
            for (j in 0 until matrixTerrain[line].size-1) {
                board += "---+"
            }
            lineCount++
            board += "---\n $lineCount "
            i++
        }
    }
    return board
}

fun makeTerrainLegend(matrixTerrain: Array<Array<Pair<String, Boolean>>>): String{
    var i = 0
    var lineCount = 1
    var board = "    ${createLegend(matrixTerrain[0].size)}    \n 1 "
    for(line in 0 until matrixTerrain.size){
        for (column in 0 until matrixTerrain[line].size){
            if(matrixTerrain[line][column].second) {
                board += " ${(matrixTerrain[line][column]).first} "
            } else {
                board += "   "
            }

            if (column < matrixTerrain[line].size-1){
                board += "|"
            }
        }
        if (i < matrixTerrain.size-1) {
            board += "\n   "
            for (j in 0 until matrixTerrain[line].size-1) {
                board += "---+"
            }
            lineCount++
            board += "---\n $lineCount "
            i++
        }
    }
    return board
}

fun makeTerrainNoLegend(matrixTerrain: Array<Array<Pair<String, Boolean>>>): String{
    var board = ""
    var i = 0
    for(line in 0 until matrixTerrain.size){
        for (column in 0 until matrixTerrain[line].size){
            if(matrixTerrain[line][column].second) {
                board += " ${(matrixTerrain[line][column]).first} "
            } else {
                board += "   "
            }

            if (column < matrixTerrain[line].size-1){
                board += "|"
            }
        }
        if (i < matrixTerrain.size-1) {
            board += "\n"
            for (j in 0 until matrixTerrain[line].size-1) {
                board += "---+"
            }
            board += "---\n"
            i++
        }
    }
    return board
}

fun makeTerrainEverything(matrixTerrain: Array<Array<Pair<String, Boolean>>>): String{
    var board = ""
    var i = 0
    for(line in 0 until matrixTerrain.size){
        for (column in 0 until matrixTerrain[line].size){
            board += " ${(matrixTerrain[line][column]).first} "
            if (column < matrixTerrain[line].size-1){
                board += "|"
            }
        }
        if (i < matrixTerrain.size-1) {
            board += "\n"
            for (j in 0 until matrixTerrain[line].size-1) {
                board += "---+"
            }
            board += "---\n"
            i++
        }
    }
    return board
}

fun makeTerrain(matrixTerrain: Array<Array<Pair<String, Boolean>>>, showLegend: Boolean = true, showEverything: Boolean = false): String{
    if(showLegend && showEverything){
        return makeTerrainLegendEverything(matrixTerrain)
    }

    if(showLegend && !showEverything){
        return makeTerrainLegend(matrixTerrain)
    }

    if(!showLegend && !showEverything){
        return makeTerrainNoLegend(matrixTerrain)
    }

    if(!showLegend && showEverything){
        return makeTerrainEverything(matrixTerrain)
    }
    return ""
}

fun play(lines: Int, columns: Int, mines: Int, showLegend: Boolean){
    var coordY = 0
    var coordX = 0
    var lost = false
    var everything = false
    val matrix = createMatrixTerrain(lines,columns, mines)
    fillNumberOfMines(matrix)
    do {
        revealMatrix(matrix, coordY, coordX)
        if(!everything) {
            println(makeTerrain(matrix, showLegend))
        }else{
            println(makeTerrain(matrix, showLegend,showEverything = true))
            everything = true
        }
        println(chooseTarget)
        val coordString = readLine()
        val coord = getCoordinates(coordString)
        var coordValid = coordValidation(matrix, showLegend, coord)
        if(coord != Pair(-1,-1)) {
            while (!movementValidation(isMovementPValid(Pair(coordY, coordX), coordValid), isCoordinateInsideTerrain(coordValid, columns, lines)) && coord != Pair(-1,-1)) {
                println(invalidResponse)
                revealMatrix(matrix, coordY, coordX)
                if(coord != Pair(-2,-2) && !everything) {
                    println(makeTerrain(matrix, showLegend))
                }else{
                    println(makeTerrain(matrix, showLegend,showEverything = true))
                    everything = true
                }
                println(chooseTarget)
                val coordString = readLine()
                val coord = getCoordinates(coordString)
                coordValid = coordValidation(matrix, showLegend, coord)
            }
            if(coord != Pair(-1,-1) && matrix[coordValid.first][coordValid.second] != Pair("*", false)) {
                matrix[coordY][coordX] = Pair(" ", true)
                coordY = coordValid.first
                coordX = coordValid.second
                matrix[coordY][coordX] = Pair("P", true)
            }else if(coord != Pair(-1,-1) && matrix[coordValid.first][coordValid.second] == Pair("*", false)){
                lost = true
            }
        }
    } while (coordValid != Pair(lines-1,columns-1) && coord != Pair(-1,-1) && matrix[coordY][coordX] != Pair("*", false) && !lost)
    println(makeTerrain(matrix, showLegend,showEverything = true))
    if(lost) {
        println("\nYou lost the game!")
    } else{
        println("\nYou win the game!")
    }
}

fun main(){
    if(startGame()) {
        println("Enter player name?")
        var name = readLine().toString()
        if(name.isEmpty()) {
            name = " "
        }
        while (!isNameValid(name)){
            println(invalidResponse)
            println("Enter player name?")
            name = readLine().toString()
        }

        println("Show legend (y/n)?")
        var legend = readLine().toString()
        legend = legend.uppercase()
        if(legend.isEmpty()) {
            legend = " "
        }
        val showLegend = showLegend(legend)

        println("How many lines?")
        var lines = readLine()?.toIntOrNull()
        lines = numLines(lines)

        println("How many columns?")
        var columns = readLine()?.toIntOrNull()
        columns = numColumns(columns)

        println("How many mines (press enter for default value)?")
        var mines = readLine()?.toIntOrNull()
        if (mines == null) {
            mines = calculateNumMinesForGameConfiguration(lines, columns)
        }
        while(mines != null && !isValidGameMinesConfiguration(lines, columns, mines)) {
            println(invalidResponse)
            println("How many mines (press enter for default value)?")
            mines = readLine()?.toIntOrNull()
            if (mines == null) {
                mines = calculateNumMinesForGameConfiguration(lines, columns)
            }
        }

        play(lines, columns, mines ?: 0, showLegend)
    }
}