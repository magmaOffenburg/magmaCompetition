package magma.tools.competition.runner;

public enum GameRunnerState {
	GAME_RUNNER_STATE_INIT,
	GAME_RUNNER_STATE_CONNECT,
	GAME_RUNNER_STATE_SERVER_START,
	GAME_RUNNER_STATE_TEAMS_START,
	GAME_RUNNER_STATE_GAME_FINISH,
	GAME_RUNNER_STATE_ERROR_TEAM,
	GAME_RUNNER_STATE_ERROR_CONNECT,
	GAME_RUNNER_STATE_ERROR_SERVER,
	GAME_RUNNER_STATE_ERROR_INIT,
	GAME_RUNNER_STATE_GAME_RUNNING,
	GAME_RUNNER_STATE_PENALTY_SHOOTOUT,
	GAME_RUNNER_STATE_RESTART;
}
