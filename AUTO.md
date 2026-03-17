# AUTO.md — Autonomous Period Guide
# 2026 FRC REBUILT™ | Team Reference Document

> Everything we know about the autonomous period, starting positions, paths, and autos.
> All coordinates are in meters, WPILib convention (blue alliance origin), blue alliance perspective.
> PathPlanner handles red alliance mirroring via the alliance supplier lambda — program everything as blue.

---

## What AUTO Is

- First **20 seconds** of the match
- FMS blocks all driver input — robot runs pre-programmed code only
- Both HUBs are **active** during AUTO (all FUEL scored = 1 pt each)
- AUTO ends at buzzer — FUEL must pass through the HUB sensor within ~3 seconds after buzzer to count
- After AUTO there is a 3-second gap before TELEOP starts (scoring assessment window)

---

## Why AUTO Matters Beyond Points

The alliance that scores **more FUEL in AUTO** has their HUB go **inactive** in SHIFT 1 of TELEOP.

This means winning AUTO is a double-edged sword:
- You score more points in AUTO
- But your HUB is dead for the first 25 seconds of TELEOP (SHIFT 1)

**FMS broadcasts** which alliance won AUTO to all OPERATOR CONSOLEs at the **start of TELEOP** as a game data string. Your code reads this once and then calculates HUB active/inactive status for each SHIFT using the match timer. You do not poll FMS mid-match — you calculate it yourself.

**Intentionally sandbagging AUTO FUEL to keep your HUB active in SHIFT 1 is legal but risky.** Refs can DQ you under G205 if they perceive you're deliberately throwing AUTO. Don't do it explicitly. Just optimize for total match points, not just AUTO points.

**Climbing in AUTO does NOT count toward winning AUTO.** Only FUEL scored in an active HUB determines the AUTO winner. Climbing in AUTO earns 15 MATCH points per robot (Level 1 only, max 2 robots) but has zero effect on HUB shift status.

---

## HUB Shift Code Logic

At TELEOP start, read FMS game data once:

```java
String gameData = DriverStation.getInstance().getGameSpecificMessage();
// gameData will indicate which alliance won AUTO
// "Red" = red alliance scored more FUEL (or was randomly selected)
// "Blue" = blue alliance scored more FUEL (or was randomly selected)
```

Then calculate HUB status per shift using match timer:

```java
// SHIFT schedule (timer counts DOWN from 2:20)
// TRANSITION SHIFT: 2:20 - 2:10  → both HUBs active
// SHIFT 1:         2:10 - 1:45  → AUTO winner = INACTIVE, loser = ACTIVE
// SHIFT 2:         1:45 - 1:20  → flipped from SHIFT 1
// SHIFT 3:         1:20 - 0:55  → same as SHIFT 1
// SHIFT 4:         0:55 - 0:30  → same as SHIFT 2
// END GAME:        0:30 - 0:00  → both HUBs active

public boolean isOurHubActive(double matchTimeRemaining, String autoWinner) {
    boolean weWonAuto = autoWinner.equals(ourAlliance); // "Red" or "Blue"

    if (matchTimeRemaining > 130) return true;  // TRANSITION SHIFT - both active
    if (matchTimeRemaining > 105) return !weWonAuto; // SHIFT 1
    if (matchTimeRemaining > 80)  return weWonAuto;  // SHIFT 2
    if (matchTimeRemaining > 55)  return !weWonAuto; // SHIFT 3
    if (matchTimeRemaining > 30)  return weWonAuto;  // SHIFT 4
    return true; // END GAME - both active
}
```

Display this on SmartDashboard so drivers know HUB status in real time.

---

## Starting Position Rules

**Rule G303-D:** BUMPERS must *overlap* the ROBOT STARTING LINE. The bumper physically crosses the line — it does not have to be flush against it.

**Rule G303-E:** Robot cannot be contacting a BUMP at match start.

**There are no assigned X/Y coordinates.** DS1, DS2, DS3 only determines which ethernet port the robot connects to on the alliance wall. It has no effect on where you physically place the robot on the field. All three alliance robots can start anywhere along the full 317.69 in (8.069 m) width of the ROBOT STARTING LINE.

**Alliance decides placement in queue** before each match. You coordinate with your alliance partners, agree on positions, then each robot selects the matching auto on the dashboard (SendableChooser) before the match starts.

**Max 8 FUEL preloaded** per robot at match start (G303-I). All three robots preloading = 24 FUEL total going into AUTO. These must be fully supported by the robot.

---

## Field Coordinates (Blue Alliance, Meters)

All coordinates from FE-2026 official field drawings, converted to WPILib convention.
Origin = bottom-left corner of carpet from above (blue alliance wall, far side from scoring table).

| Landmark | X (m) | Y (m) | Notes |
|---|---|---|---|
| Blue alliance wall inner face | 0.394 | — | Diamond plate face |
| Blue ROBOT STARTING LINE | 4.428 | — | BUMPERS must overlap this |
| Blue FUEL cluster center | 4.636 | 4.035 | Near edge of neutral zone FUEL |
| Blue HUB center | 4.428 | 4.035 | Opening at 72 in (1.829 m) off floor |
| CENTER LINE | 8.271 | — | Do not fully cross in AUTO (G403) |
| Red FUEL cluster center | 12.169 | 4.035 | — |
| Red ROBOT STARTING LINE | 12.113 | — | — |
| Field size | 16.541 | 8.069 | Full carpet dimensions |
| FUEL cluster size | 2.310 | 2.310 | Bounding box ~90.95 × 90.95 in |

### Driver Station Y Positions (approximate, blue alliance)

DS width ≈ 82.23 in (2.089 m) each. Scoring table is at the TOP of the field (high Y).

| Position | Y center (m) | Notes |
|---|---|---|
| DS1 | 5.222 | Near scoring table (high Y) |
| DS2 | 3.133 | Center — directly in front of HUB |
| DS3 | 1.044 | Far from scoring table (low Y) |

### Starting Poses (robot center, bumpers overlapping RSL)

Robot assumed ~29 in deep in starting config. Center = RSL - 14.5 in = 4.428 - 0.368 = 4.060 m.

```java
// DS1 — near scoring table
new Pose2d(4.060, 5.222, Rotation2d.fromDegrees(0))

// DS2 — center, directly in front of HUB (best scoring position)
new Pose2d(4.060, 3.133, Rotation2d.fromDegrees(0))

// DS3 — far from scoring table
new Pose2d(4.060, 1.044, Rotation2d.fromDegrees(0))
```

Rotation 0° = robot facing positive X (toward red alliance). Adjust based on shooter orientation.

---

## AUTO Rules That Affect Path Design

**G401:** No driver input in AUTO. Any contact with controls = MINOR FOUL + YELLOW CARD.

**G403:** If robot BUMPERS are completely across the CENTER LINE (x > 8.271 m for blue), robot cannot contact an opponent robot. MAJOR FOUL. Partial crossing (bumpers not fully across) is fine. Design paths to stay under x ≈ 7.8 m to be safe.

**G407:** To score FUEL into HUB, BUMPERS must be partially or fully in ALLIANCE ZONE (x ≤ 4.428 m for blue). Robot cannot shoot from NEUTRAL ZONE. All shooting waypoints must end with robot at x ≤ ~4.0 m.

**G408 (catch rule):** Cannot collect FUEL coming directly out of HUB exits until it contacts something else first. Carpet counts as "something else." FUEL that hits the floor near the HUB exit is legal to collect. Do not design paths that sit the robot directly under a HUB exit to collect the stream.

**G402:** Human player CAN deliver FUEL to robots during AUTO (entering FUEL onto field is an explicit exception). Human player can feed FUEL through OUTPOST CHUTE during AUTO.

**Climbing in AUTO:** Legal, earns 15 pts per robot at LEVEL 1 (max 2 robots). Does not affect AUTO winner determination. Robot must be off carpet and off TOWER BASE to qualify.

**If robot falls in AUTO:** Match continues. No restart. Robot stays where it fell for the rest of AUTO. Design robust — if your robot tips, it's done for 20 seconds.

---

## Paths to Build (PathPlanner .path files)

Name format: `StartPosition-Action-EndPosition`

### Tier 1 — Build First

| Path File | Start (m) | End (m) | Purpose |
|---|---|---|---|
| `DS2-Mobility.path` | 4.060, 3.133 | 6.0, 3.133 | Simple forward drive, cross RSL |
| `DS2-to-FuelCluster.path` | 4.060, 3.133 | 5.2, 4.035 | Drive to near FUEL cluster |
| `FuelCluster-to-HUB-DS2.path` | 5.2, 4.035 | 3.8, 3.133 | Return to shooting position |

### Tier 2 — Build After Tier 1 Works

| Path File | Start (m) | End (m) | Purpose |
|---|---|---|---|
| `DS1-to-FuelCluster-Top.path` | 4.060, 5.222 | 5.2, 6.0 | DS1 collect near scoring table side |
| `FuelCluster-Top-to-HUB-DS1.path` | 5.2, 6.0 | 3.8, 5.222 | DS1 return to shoot |
| `DS3-to-FuelCluster-Bot.path` | 4.060, 1.044 | 5.2, 2.0 | DS3 collect far side |
| `FuelCluster-Bot-to-HUB-DS3.path` | 5.2, 2.0 | 3.8, 1.044 | DS3 return to shoot |
| `DS2-DeepCollect.path` | 3.8, 3.133 | 7.0, 4.035 | Deeper into neutral zone for 2-piece |

### Tier 3 — Advanced

| Path File | Notes |
|---|---|
| `DS1-UnderTrench.path` | Only if robot ≤ 22.25 in tall with mechanisms. TRENCH clearance = 22.25 in × 50.34 in. Use TRENCH AprilTags (35 in height) for mid-path re-localization. |

**Mirroring:** Build DS2 paths first. Use PathPlanner's Flip Path button to mirror for DS1/DS3 variants where geometry allows. Always verify flipped paths visually before running on robot.

---

## Autos to Build (PathPlanner .auto files)

### Priority Order

**1. `MobilityOnly-DS2.auto`** — Easiest, build first
- Start: DS2
- Sequence: `DS2-Mobility.path`
- Purpose: Fallback. Proves pathing works. Have DS1 and DS3 variants.

**2. `Preload-DS2.auto`** — No movement
- Start: DS2
- Sequence: `ShootPreload` command
- Purpose: Reliable 8 points if pathing is broken. Safe fallback at competition.

**3. `1Piece-DS2.auto`** — Core auto
- Start: DS2 (4.060, 3.133)
- Sequence:
  1. `ShootPreload` — shoot 8 preloaded FUEL into HUB
  2. `DS2-to-FuelCluster.path` + event marker at 20%: `StartIntake`
  3. Wait ~0.3s for intake to grab FUEL
  4. `FuelCluster-to-HUB-DS2.path` + event marker at 60%: `SpinUpShooter`
  5. `ShootCollected` — shoot collected FUEL
- Constraints: end pose of shoot must have x ≤ 4.428 (in ALLIANCE ZONE per G407)

**4. `1Piece-DS1.auto`** and **`1Piece-DS3.auto`**
- Same sequence as DS2 variant, different starting Y and corresponding paths
- Critical for competition — alliance placement varies every match

**5. `2Piece-DS2.auto`** — Build after 1Piece is reliable
- Start: DS2
- Sequence:
  1. `ShootPreload`
  2. `DS2-to-FuelCluster.path` + `StartIntake`
  3. `FuelCluster-to-HUB-DS2.path` + `SpinUpShooter`
  4. `ShootCollected`
  5. `DS2-DeepCollect.path` + `StartIntake`
  6. `FuelCluster-to-HUB-DS2.path` + `SpinUpShooter`
  7. `ShootCollected`
- Risk: deep collect goes to x ≈ 7.0 m — keep bumpers away from CENTER LINE (8.271 m)

**6. `1Piece-DS1-Trench.auto`** — Only if robot fits under TRENCH
- Verify robot height ≤ 22.25 in (56.5 cm) with all mechanisms before building

---

## Named Commands (register before AutoBuilder.buildAutoChooser())

```java
// In RobotContainer constructor, BEFORE AutoBuilder.buildAutoChooser()
NamedCommands.registerCommand("ShootPreload", new ShootCommand(shooter, 8));
NamedCommands.registerCommand("ShootCollected", new ShootCommand(shooter));
NamedCommands.registerCommand("StartIntake", new StartIntakeCommand(intake));
NamedCommands.registerCommand("StopIntake", new StopIntakeCommand(intake));
NamedCommands.registerCommand("SpinUpShooter", new SpinUpShooterCommand(shooter));
```

---

## AutoBuilder Configuration (swerve)

```java
AutoBuilder.configureHolonomic(
    drivetrain::getPose,
    drivetrain::resetOdometry,
    drivetrain::getRobotRelativeSpeeds,
    drivetrain::driveRobotRelative,
    new HolonomicPathFollowerConfig(
        new PIDConstants(5.0, 0.0, 0.0),  // translation PID — tune these
        new PIDConstants(5.0, 0.0, 0.0),  // rotation PID — tune these
        4.5,                               // max module speed m/s
        drivebaseRadius,                   // center to furthest module (meters)
        new ReplanningConfig()
    ),
    // Alliance flip — returns true when on red alliance
    () -> DriverStation.getAlliance().orElse(Alliance.Blue) == Alliance.Red,
    drivetrain
);
```

---

## Dashboard / SendableChooser

```java
// Build chooser — reads all .auto files from deploy/pathplanner/autos/
autoChooser = AutoBuilder.buildAutoChooser();
SmartDashboard.putData("Auto Chooser", autoChooser);

// Also display HUB status during TELEOP
SmartDashboard.putBoolean("HUB Active", isOurHubActive(...));
SmartDashboard.putString("Auto Winner", gameData);
```

---

## File Structure

```
src/main/deploy/pathplanner/
  paths/
    DS2-Mobility.path
    DS2-to-FuelCluster.path
    FuelCluster-to-HUB-DS2.path
    DS1-to-FuelCluster-Top.path
    FuelCluster-Top-to-HUB-DS1.path
    DS3-to-FuelCluster-Bot.path
    FuelCluster-Bot-to-HUB-DS3.path
    DS2-DeepCollect.path
    DS1-UnderTrench.path           (if robot fits)
  autos/
    MobilityOnly-DS2.auto
    MobilityOnly-DS1.auto
    MobilityOnly-DS3.auto
    Preload-DS2.auto
    Preload-DS1.auto
    Preload-DS3.auto
    1Piece-DS2.auto
    1Piece-DS1.auto
    1Piece-DS3.auto
    2Piece-DS2.auto
    1Piece-DS1-Trench.auto         (if robot fits)
```

---

## Build Order

1. Configure `AutoBuilder` — nothing works without this
2. Build `DS2-Mobility.path` + `MobilityOnly-DS2.auto` — prove pathing works
3. Register `NamedCommands` — must happen before `buildAutoChooser()`
4. Build `Preload-DS2.auto` — prove commands fire in auto
5. Build `DS2-to-FuelCluster.path` + `FuelCluster-to-HUB-DS2.path`
6. Assemble `1Piece-DS2.auto` — verify event markers fire at correct path %
7. Build DS1 and DS3 variants — critical for competition placement flexibility
8. Build `2Piece-DS2.auto` — only after 1Piece is reliable
9. Build Trench paths — only if robot height confirmed ≤ 22.25 in

---

## Key Constraints Checklist for Every Path

- [ ] Shooting waypoints end at x ≤ 4.428 m (BUMPERS in ALLIANCE ZONE, G407)
- [ ] Deep collect paths keep x < 8.271 m (don't fully cross CENTER LINE, G403)
- [ ] No path sits robot under HUB exit stream (G408 catch rule — carpet contact fixes this)
- [ ] Starting pose seeds odometry correctly at match start
- [ ] Alliance flip lambda is configured — red alliance paths mirror automatically
- [ ] All NamedCommands registered before buildAutoChooser() is called

---

*Sources: 2026 FRC REBUILT Game Manual TU16, FE-2026 Field Drawings, Diego (manual analyst), confirmed answers March 2026.*
