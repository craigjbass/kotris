package uk.co.craigbass.kotris

import uk.co.craigbass.kotris.KotrisBoard.BoardPresenter.*

class KotrisBoard() {
    private var movingPiece: IPiece? = null
    private var boardPresenter: BoardPresenter? = null

    fun play(p: BoardPresenter) {
        boardPresenter = p
        present()
    }

    fun advance() {
        this.movingPiece = IPiece(5, -3)
        present()
    }

    class IPiece(val positionX: Int, val positionY: Int) {
        fun occludes(x:Int, y: Int):Boolean {
            val bottom = positionY + 4
            return positionX == x && bottom == y
        }
    }

    private fun present() {
        val rows = (1..20).map {
            y: Int ->
            val cells = (1..10).map {
                x: Int ->
                if (movingPiece?.occludes(x,y) ?: false) Cell() else null
            }
            Row(cells)
        }
        boardPresenter?.present(Board(rows))
    }

    interface BoardPresenter {
        fun present(board: Board)
        class Board(var rows: List<Row>) {}
        class Row(var cells: List<Cell?>) {}
        class Cell {}
    }
}
