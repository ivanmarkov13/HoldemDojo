package com.nedogeek.holdem.dealer;

import com.nedogeek.holdem.GameSettings;
import com.nedogeek.holdem.GameStatus;
import com.nedogeek.holdem.connections.PlayersAction;
import com.nedogeek.holdem.gamingStuff.Desk;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * User: Konstantin Demishev
 * Date: 05.10.12
 * Time: 22:02
 */
public class DealerTest {
    private final int COINS_AT_START = 1000;
    private final int SMALL_BLIND = 10;

    private Dealer dealer;
    private Desk deskMock;
    private PlayersAction playersActionMock;

    @Before
    public void setUp() throws Exception {
        resetPlayerActionMock();
        resetDeskMock();

        createDealer();
    }

    private void resetPlayerActionMock() {
        playersActionMock = mock(PlayersAction.class);
    }

    private void createDealer() {
        dealer = new Dealer(deskMock);
    }

    private void resetDeskMock() {
        deskMock = mock(Desk.class);
        setGameStatus(GameStatus.Started);
        int PLAYERS_QUANTITY = 2;
        setPlayersQuantity(PLAYERS_QUANTITY);
        setDealerPlayerNumber(-1);
        when(deskMock.getPlayerAmount(anyInt())).thenReturn(COINS_AT_START);
        when(deskMock.getLastMovedPlayer()).thenReturn(-1);
        setResponseFold();
        when(deskMock.getPlayersMove(anyInt())).thenReturn(playersActionMock);
    }

    private void setResponseType(PlayersAction.ActionType actionType) {
        when(playersActionMock.getActionType()).thenReturn(actionType);
    }

    private void setFirstRound() {
        when(deskMock.getGameRound()).thenReturn(1);
        when(deskMock.getDealerPlayerNumber()).thenReturn(0);
        when(deskMock.getGameStatus()).thenReturn(GameStatus.Started);

        when(deskMock.getPlayerBet(1)).thenReturn(SMALL_BLIND);
        when(deskMock.getPlayerAmount(1)).thenReturn(COINS_AT_START - SMALL_BLIND);

        when(deskMock.getPlayerBet(0)).thenReturn(2 * SMALL_BLIND);
        when(deskMock.getPlayerAmount(0)).thenReturn(COINS_AT_START - 2 * SMALL_BLIND);

        when(deskMock.getLastMovedPlayer()).thenReturn(-1);

        setCallValue(SMALL_BLIND);
    }

    private void setCallValue(int minimumBet) {
        when(deskMock.getCallValue()).thenReturn(minimumBet);
    }

    private void setResponseBet(int bet) {
        setResponseType(PlayersAction.ActionType.Bet);
        when(playersActionMock.getBetQuantity()).thenReturn(bet);
    }

    private void setResponseFold() {
        setResponseType(PlayersAction.ActionType.Fold);
    }

    private void setResponseCall() {
        setResponseType(PlayersAction.ActionType.Call);
    }

    private void setPlayersBet(int playerNumber, int playersBet) {
        when(deskMock.getPlayerBet(playerNumber)).thenReturn(playersBet);
    }

    private void setLastPlayerMoved(int movedPlayerNumber) {
        when(deskMock.getLastMovedPlayer()).thenReturn(movedPlayerNumber);
    }

    private void setPlayerAmount(int playerNumber, int amount) {
        when(deskMock.getPlayerAmount(playerNumber)).thenReturn(amount);
    }

    private void setDealerPlayerNumber(int dealerPlayerNumber) {
        when(deskMock.getDealerPlayerNumber()).thenReturn(dealerPlayerNumber);
    }

    private void setPlayersQuantity(int PLAYERS_QUANTITY) {
        when(deskMock.getPlayersQuantity()).thenReturn(PLAYERS_QUANTITY);

        createDealer();
    }

    private void setGameStatus(GameStatus newGameStatus) {
        when(deskMock.getGameStatus()).thenReturn(newGameStatus);
    }

    private void setGameRound(int newGameRound) {
        when(deskMock.getGameRound()).thenReturn(newGameRound);
    }

    @Test
    public void shouldEngineNotNull() throws Exception {
        assertNotNull(dealer);
    }

    @Test
    public void shouldDeskIsGamePossibleWhenStart() throws Exception {
        dealer.run();

        verify(deskMock, atLeast(1)).getGameStatus();
    }

    @Test
    public void shouldNotSetGameStatusStartedWhenGameStatusNotReady() throws Exception {
        setGameStatus(GameStatus.Not_Ready);

        dealer.run();

        verify(deskMock, never()).setGameStatus(GameStatus.Started);
    }

    @Test
    public void shouldSetGameStatusStartedWhenGameStatusReady() throws Exception {
        setGameStatus(GameStatus.Ready);

        dealer.run();

        verify(deskMock).setGameStatus(GameStatus.Started);
    }

    @Test
    public void shouldGetPlayersQuantityWhenGameStarted() throws Exception {
        setGameStatus(GameStatus.Ready);

        dealer.run();

        verify(deskMock, atLeast(1)).getPlayersQuantity();
    }

    @Test
    public void shouldFirstPlayerSetDefaultAmountWhenGameStarted() throws Exception {
        setGameStatus(GameStatus.Ready);

        dealer.tick();

        verify(deskMock).setPlayerAmount(0, GameSettings.COINS_AT_START);
    }

    @Test
    public void shouldSecondPlayerSetDefaultAmountWhenGameStarted() throws Exception {
        setGameStatus(GameStatus.Ready);

        dealer.run();

        verify(deskMock).setPlayerAmount(1, GameSettings.COINS_AT_START);
    }

    @Test
    public void shouldSetDealerPlayer0WhenStartGaming() throws Exception {
        dealer.run();

        verify(deskMock).setDealerPlayer(0);
    }

    @Test
    public void shouldShuffleCardsWhenStartGaming() throws Exception {
        dealer.run();

        verify(deskMock).shuffleCards();
    }

    @Test
    public void shouldSecondPlayerGiveSmallBlindWhenGameStarted() throws Exception {
        dealer.run();

        verify(deskMock).setPlayerBet(1, GameSettings.SMALL_BLIND_AT_START);
    }

    @Test
    public void shouldFirstPlayerGiveBigBlindWhenGameStarted() throws Exception {
        setGameStatus(GameStatus.Started);

        dealer.tick();

        verify(deskMock).setPlayerBet(0, GameSettings.SMALL_BLIND_AT_START * 2);
    }

    @Test
    public void shouldThirdPlayerGiveBigBlindWhenGameStartedWith3Players() throws Exception {
        setPlayersQuantity(3);

        dealer.tick();

        verify(deskMock).setPlayerBet(2, GameSettings.SMALL_BLIND_AT_START * 2);
    }

    @Test
    public void shouldSetDealerPlayerNumber1WhenPreviousDealerPlayerNumberWas0() throws Exception {
        setDealerPlayerNumber(0);

        dealer.run();

        verify(deskMock).setDealerPlayer(1);
    }

    @Test
    public void shouldSmallBlindAddedToPotWhenGameStarted() throws Exception {
        dealer.run();

        verify(deskMock).addToPot(GameSettings.SMALL_BLIND_AT_START);
    }

    @Test
    public void shouldBigBlindAddedToPotWhenGameStarted() throws Exception {
        dealer.run();

        verify(deskMock).addToPot(GameSettings.SMALL_BLIND_AT_START * 2);
    }

    @Test
    public void shouldSecondPlayerAmountMinusSmallBlindWhenGameStarted() throws Exception {
        dealer.run();

        verify(deskMock).setPlayerAmount(1, GameSettings.COINS_AT_START - GameSettings.SMALL_BLIND_AT_START);
    }

    @Test
    public void shouldBet5WhenFirstPlayerHasOnly5Coins() throws Exception {
        setPlayerAmount(0, 5);

        dealer.tick();

        verify(deskMock).setPlayerBet(0, 5);
    }

    @Test
    public void shouldFirstPlayerAmountIs0HasOnly5Coins() throws Exception {
        setPlayerAmount(0, 5);

        dealer.run();

        verify(deskMock).setPlayerAmount(0, 0);
    }

    @Test
    public void shouldFirstPlayerMoveRequestWhenNewGameStartedAndDealerIsSecond() throws Exception {
        setGameRound(1);
        setDealerPlayerNumber(1);

        dealer.tick();

        verify(deskMock).getPlayersMove(0);
    }

    @Test
    public void shouldSecondPlayerMoveRequestWhenTickAndNewGameSet() throws Exception {
        setGameRound(1);
        setDealerPlayerNumber(0);

        dealer.tick();

        verify(deskMock).getPlayersMove(1);
    }

    @Test
    public void shouldNoGetPlayerMoveWhenStatusNotReadyAndTick() throws Exception {
        setGameStatus(GameStatus.Not_Ready);

        dealer.tick();

        verify(deskMock, never()).getPlayersMove(1);
    }

    @Test
    public void shouldGetGameRoundNumberWhenTick() throws Exception {
        dealer.tick();

        verify(deskMock).getGameRound();
    }

    @Test
    public void shouldNotGetGameRoundNumberWhenTickAndGameStatusNotReady() throws Exception {
        setGameStatus(GameStatus.Not_Ready);

        dealer.tick();

        verify(deskMock, never()).getGameRound();
    }

    @Test
    public void shouldSetGameRound1WhenTick() throws Exception {
        dealer.tick();

        verify(deskMock).setNextGameRound();
    }

    @Test
    public void shouldNotSetGameRound1WhenTickGameRoundIs1() throws Exception {
        setGameRound(1);

        dealer.tick();

        verify(deskMock, never()).setNextGameRound();
    }

    @Test
    public void shouldMoveSecondPlayerWhenFirstPlayerMovedLastFirstRoundFirstPlayerBet100Second50() throws Exception {
        setFirstRound();

        dealer.tick();

        verify(deskMock).getPlayersMove(1);
    }

    @Test
    public void shouldSetLastMovedPlayer1WhenFirstPlayerMovedLastFirstRoundFirstPlayerBet100Second50AndPlayerActionIsBet500() throws Exception {
        setFirstRound();

        dealer.tick();

        verify(deskMock).setLastMovedPlayer(1);
    }


    @Test
    public void shouldBet50WhenFirstPlayerMovedLastFirstRoundFirstPlayerBet50Second50AndPlayerActionIsBet500() throws Exception {
        setFirstRound();

        setResponseBet(2 * SMALL_BLIND);

        dealer.tick();

        verify(deskMock).setPlayerBet(1, 2 * SMALL_BLIND);
    }

    @Test
    public void shouldMovedFirstPlayerWhenDefaultFirstRound() throws Exception {
        setFirstRound();

        dealer.tick();

        verify(deskMock).getPlayersMove(1);
    }

    @Test
    public void shouldSecondPlayerFoldWhenFirstRoundAndHeFolds() throws Exception {
        setFirstRound();

        setResponseFold();

        dealer.tick();

        verify(deskMock).setPlayerFold(1);
    }

    @Test
    public void shouldFirstPlayerFoldWhenFirstRoundAndFirstMovesAndHeFolds() throws Exception {
        setFirstRound();

        setPlayersBet(1,2 * SMALL_BLIND);
        setLastPlayerMoved(1);

        setResponseFold();

        dealer.tick();

        verify(deskMock).setPlayerFold(0);
    }

    @Test
    public void shouldNoSecondPlayerFoldWhenFirstRoundAndHeBet2SmallBlinds() throws Exception {
        setFirstRound();

        setResponseBet(2 * SMALL_BLIND);

        dealer.tick();

        verify(deskMock, never()).setPlayerFold(1);
    }

    @Test
    public void shouldSecondPlayerBet1WhenFirstRoundAndSecondCalls() throws Exception {
        setFirstRound();

        setResponseCall();

        dealer.tick();

        verify(deskMock).setPlayerBet(1, SMALL_BLIND);
    }

    @Test
    public void shouldNoSecondPlayerBet0WhenFirstRoundAndHeFolds() throws Exception {
        setFirstRound();

        setResponseFold();

        dealer.tick();

        verify(deskMock, never()).setPlayerBet(1, 0);
    }

    @Test
    public void shouldSecondPlayerBet2SmallBlindWhenCallInFirstRoundWithCallValue2SmallBlind() throws Exception {
        setFirstRound();
        setCallValue(2 * SMALL_BLIND);

        setResponseCall();

        dealer.tick();

        verify(deskMock).setPlayerBet(1, 2 * SMALL_BLIND);
    }

    /*
       Что бы еще потестить:
       Круг закрывается, когда одинаковое кол-во ставок.
       Круг закрывается, когда остался один не фолданувший.

       Если игрок фолданул - нужно ставить ему соответствующий статуус.       ok
       Если игрок заколил - нужно фигачить сумму минимальную для хода игры.
       Если игрок ушел в оллин - нужно делать максимальную ставку.
       Если игрок заколил, но у него не хватает фишек - нужно делать оллин.
       Если игрок райзанул, но этого не хватает - это фолд.
       Если игрок чеканул, но этого нельзя было делать - это фолд.
       Если игрок райзанул до предела, когда у него не хватает фишек на разй или ровно столько - это оллин.

       Если у игрока оллин - то не нужно его трогать.
       Ход не должен переходить к фолданувшему игроку.


       Игра заканчивается, когда остался один фолданувший.
       Игра заканчивается, когда закрылся 3-й круг.

       Конец игры будет 4-й этап. И в любом случае нужно идти от него..
       Кстати, желательно, чтобы был такой метод, типа goToEndStage()

       Если выиграл игрок с оллин - то нужно отдать ему только ту часть денег, на которую он заслуживает.
       Остальные же деньги нужно разыгрывать между другими игроками.
       А среди них тоже может быть тот, кто оллин сказал. Правило такое - банки нужно делить по очереди.
       Вот как. Т.е. должна быть возможность создания многих банков...


       А после того, как заканчивается игра - нужно выполнить чего-то типа newGame()
       Но, если ни у кого фишек не осталось - нужно закончить игровой цикл, записать победителя,
       выбрать нового диллера и заново.
    */
}
