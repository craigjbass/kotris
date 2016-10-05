package uk.co.craigbass.kotris.test.unit

import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import uk.co.craigbass.kotris.KotrisBoard
import uk.co.craigbass.kotris.KotrisBoard.BoardPresenter
import uk.co.craigbass.kotris.KotrisBoard.BoardPresenter.*
import uk.co.craigbass.kotris.test.unit.VerifyingBoardPresenter.ExpectedCellPresence.*

class KotrisBoardSpec : Spek({
    describe("kotris board") {
        fun play(): VerifyingBoardPresenter {
            val presenter = VerifyingBoardPresenter()
            KotrisBoard().play(presenter)
            return presenter
        }

        it("presents a board") {
            play().hasReceivedBoard()
        }

        it("presents an empty board with 20 rows") {
            play().has20Rows()
        }

        it("presents an empty board with 20 rows each of size 10") {
            play().hasOnlyRowsOfSize10()
        }

        it("inserts a new kotromino when the board is empty") {
            val presenter = VerifyingBoardPresenter()
            val kotrisBoard = KotrisBoard()
            kotrisBoard.play(presenter)
            kotrisBoard.advance()

            presenter.verifyPresenceOfCells(listOf(
                    listOf(`-`, `-`, `-`, `-`, `P`, `-`, `-`, `-`, `-`, `-`),
                    listOf(`-`, `-`, `-`, `-`, `-`, `-`, `-`, `-`, `-`, `-`)
            ))
        }

        it("the kotromino falls when the board is advanced") {
            val presenter = VerifyingBoardPresenter()
            val kotrisBoard = KotrisBoard()
            kotrisBoard.play(presenter)
            kotrisBoard.advance()
            kotrisBoard.advance()

            presenter.verifyPresenceOfCells(listOf(
                    listOf(`-`, `-`, `-`, `-`, `P`, `-`, `-`, `-`, `-`, `-`),
                    listOf(`-`, `-`, `-`, `-`, `P`, `-`, `-`, `-`, `-`, `-`),
                    listOf(`-`, `-`, `-`, `-`, `-`, `-`, `-`, `-`, `-`, `-`)
            ))
        }
    }
})

class VerifyingBoardPresenter(var board: Board? = null) : BoardPresenter {
    override fun present(board: Board) {
        this.board = board
    }

    private fun print(board: Board) {
        val border = "-".repeat(10) + "\n"
        print(border)
        board.rows.forEach {
            it.cells.forEach { print( if( it == null ) " " else "#" ) }
            print("\n")
        }
        print(border)
    }

    fun hasReceivedBoard() {
        board.should.not.be.`null`
    }

    fun has20Rows() {
        board?.rows.should.be.size.equal(20)
    }

    fun hasOnlyRowsOfSize10() {
        board?.rows?.map {
            it.cells.should.be.size.equal(10)
        }
    }

    fun fetchCell(x: Int, y: Int): Cell? {
        return board?.rows?.get(y - 1)?.cells?.get(x - 1)
    }

    enum class ExpectedCellPresence {
        P, `-`
    }

    fun verifyPresenceOfCells(cells: List<List<ExpectedCellPresence>>) {
        cells.mapIndexed { y, list ->
            list.mapIndexed { x, expectedPresence ->
                assertCellPresenceIs(
                        present = expectedPresence == P,
                        currentCell = fetchCell(x + 1, y + 1),
                        assertionCells = cells
                )
            }
        }
    }

    private fun assertCellPresenceIs(present: Boolean,
                                     currentCell: Cell?,
                                     assertionCells: List<List<ExpectedCellPresence>>) {
        val notPresent = currentCell == null
        val expectsPresence = present
        val failed = expectsPresence && notPresent
        if (failed) printExpectationError(assertionCells)
        failed.should.be.`false`
    }

    private fun printExpectationError(assertionCells: List<List<ExpectedCellPresence>>) {
        println("Board was:")
        this.print(board!!)
        println("But expected:")
        this.print(convertToBoard(assertionCells))
    }

    private fun convertToBoard(assertionCells: List<List<ExpectedCellPresence>>) = Board(assertionCells.map { Row(it.map { if (it == P) Cell() else null }) })
}
