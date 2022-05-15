import kotlin.math.abs

const val invalidResponse = "Invalid response.\n"
const val chooseTarget = "\nChoose the Target cell (e.g 2D)"

fun makeMenu(): String = "\nWelcome to DEISI Minesweeper\n\n1 - Start New Game\n0 - Exit Game\n"

fun startGame(): Boolean{
    var start = readLine()?.toIntOrNull() ?: 2
    while(start != 1 && start != 0){
        println(invalidResponse)
        println(makeMenu())
        start = readLine()?.toIntOrNull() ?: 2
    }
    if(start == 1) {
        return true
    }
    return false
}

fun checkLength(name: String, minLength: Int): Boolean{
    var i = 0
    var space = 0
    while(i < name.length && space == 0){
        if(name[i] == ' ') {
            space++
        }
        else {
            i++
        }
    }
    if (space > 0 && i >= minLength) {
        return true
    }
    return false
}

fun checkSurname(surname: String): Boolean{
    var i = 1
    if (surname.first().isLowerCase()) {
        return false
    }
    while (i < surname.length){
        if (surname[i].isUpperCase()) {
            return false
        }
        i++
    }
    return true
}

fun checkSurnames(name: String): Boolean{
    var i = 0
    var surname = ""
    while (i < name.length){
        if (i == name.length - 1 && name[i] == ' ') {
            return false
        }
        else if(name[i] == ' '){
            if (!checkSurname(surname)) {
                return false
            }
            else {
                surname = ""
                i++
            }
        }
        surname += name[i]
        i++
        if (i == name.length){
            if (!checkSurname(surname)) {
                return false
            }
            else {
                surname = ""
            }
        }
    }
    return true
}

fun isNameValid(name: String?, minLength: Int = 3): Boolean{
    if(name != null && checkLength(name, minLength) && checkSurnames(name)){
        return true
    }
    return false
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

fun getSquareAroundPoint(linha: Int, coluna: Int, numLines: Int, numColumns: Int): Pair<Pair<Int, Int>, Pair<Int, Int>> {
    var yLeft = linha - 1
    var yRight = linha + 1
    var xLeft = coluna - 1
    var xRight = coluna + 1
    when(coluna){
        0 -> xLeft = 0
        numColumns-1 -> xRight = numColumns-1
    }
    when(linha){
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
    for(linha in yl..yr){
        for (coluna in xl..xr){
            if ((matrixTerrain[linha][coluna] == Pair("*", false)
                        || matrixTerrain[linha][coluna] == Pair("*", true)
                        || matrixTerrain[linha][coluna] == Pair("P", true) && (matrixTerrain[centerY][centerX] != Pair("P", true))
                        || matrixTerrain[linha][coluna] == Pair("f", true))
                && matrixTerrain[linha][coluna] != matrixTerrain[centerY][centerX]) {

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

    for(linha in square.first.first..square.second.first){
        for (coluna in square.first.second..square.second.second){
            val mines = countNumberOfMinesCloseToCurrentCell(matrixTerrain,coordY,coordX)
            if (endGame) {
                matrixTerrain[linha][coluna] = Pair(" ", true)
            }
            if(matrixTerrain[linha][coluna].first == " " && !endGame){
                matrixTerrain[linha][coluna] = Pair(" ", true)
            }
            if(matrixTerrain[linha][coluna].first == "$mines" && !endGame){
                matrixTerrain[linha][coluna] = Pair("$mines", true)
            }
        }
    }
}

fun fillNumberOfMines(matrixTerrain: Array<Array<Pair<String, Boolean>>>){
    for(linha in 0 until matrixTerrain.size){
        for (coluna in 0 until matrixTerrain[linha].size){
            val mines = countNumberOfMinesCloseToCurrentCell(matrixTerrain, linha, coluna)
            if(mines > 0 && (matrixTerrain[linha][coluna] == Pair(" ", true) || matrixTerrain[linha][coluna] == Pair(" ", false))){
                matrixTerrain[linha][coluna] = Pair("$mines", false)
            }
        }
    }
}

fun getNumLines(matrixTerrain: Array<Array<Pair<String, Boolean>>>): Int{
    var numLines = 0
    for(linha in 0 until matrixTerrain.size){
        numLines++
    }
    return numLines
}

fun getNumColumns(matrixTerrain: Array<Array<Pair<String, Boolean>>>): Int{
    var numColumns = 0
    for(linha in 0 until 1){
        for (coluna in 0 until matrixTerrain[linha].size){
            numColumns++
        }
    }
    return numColumns
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
    for(linha in yLeft..yRight){
        for (coluna in xLeft..xRight){
            if (matrixTerrain[linha][coluna] == Pair("*", false) || matrixTerrain[linha][coluna] == Pair("*", true)) {
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
    var line = 1
    var board = "    ${createLegend(matrixTerrain[0].size)}    \n 1 "
    for(linha in 0 until matrixTerrain.size){
        for (coluna in 0 until matrixTerrain[linha].size){
            board += " ${(matrixTerrain[linha][coluna]).first} "
            if (coluna < matrixTerrain[linha].size-1){
                board += "|"
            }
        }
        if (i < matrixTerrain.size-1) {
            board += "\n   "
            for (j in 0 until matrixTerrain[linha].size-1) {
                board += "---+"
            }
            line++
            board += "---\n $line "
            i++
        }
    }
    return board
}

fun makeTerrainLegend(matrixTerrain: Array<Array<Pair<String, Boolean>>>): String{
    var i = 0
    var line = 1
    var board = "    ${createLegend(matrixTerrain[0].size)}    \n 1 "
    for(linha in 0 until matrixTerrain.size){
        for (coluna in 0 until matrixTerrain[linha].size){
            if(matrixTerrain[linha][coluna].second) {
                board += " ${(matrixTerrain[linha][coluna]).first} "
            } else {
                board += "   "
            }

            if (coluna < matrixTerrain[linha].size-1){
                board += "|"
            }
        }
        if (i < matrixTerrain.size-1) {
            board += "\n   "
            for (j in 0 until matrixTerrain[linha].size-1) {
                board += "---+"
            }
            line++
            board += "---\n $line "
            i++
        }
    }
    return board
}

fun makeTerrainNoLegend(matrixTerrain: Array<Array<Pair<String, Boolean>>>): String{
    var board = ""
    var i = 0
    for(linha in 0 until matrixTerrain.size){
        for (coluna in 0 until matrixTerrain[linha].size){
            if(matrixTerrain[linha][coluna].second) {
                board += " ${(matrixTerrain[linha][coluna]).first} "
            } else {
                board += "   "
            }

            if (coluna < matrixTerrain[linha].size-1){
                board += "|"
            }
        }
        if (i < matrixTerrain.size-1) {
            board += "\n"
            for (j in 0 until matrixTerrain[linha].size-1) {
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
    for(linha in 0 until matrixTerrain.size){
        for (coluna in 0 until matrixTerrain[linha].size){
            board += " ${(matrixTerrain[linha][coluna]).first} "
            if (coluna < matrixTerrain[linha].size-1){
                board += "|"
            }
        }
        if (i < matrixTerrain.size-1) {
            board += "\n"
            for (j in 0 until matrixTerrain[linha].size-1) {
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
    println(makeMenu())
    val start = startGame()
    if(start) {
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