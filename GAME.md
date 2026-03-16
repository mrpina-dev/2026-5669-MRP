# REBUILT 2026 — Game Context for FRC Robot Code

> 2026 FRC Game: REBUILT™ presented by Haas | Manual version TU16 | Kickoff: January 10, 2026
> This file provides complete game context so AI assistants and contributors can work on robot code without needing to re-read the full manual.

---

## Game Summary

Two alliances (red and blue, 3 robots each) score FUEL (5.91 in foam balls) into their HUB, cross BUMPS and TRENCHES, and climb a TOWER. A key mechanic: the alliance that scores more FUEL in AUTO has their HUB go **inactive first** in TELEOP, creating alternating active/inactive windows across four 25-second SHIFTs. END GAME reopens both HUBs and is when robots climb.

This is a re-creation of the 2020 FRC game Infinite Recharge.

---

## Match Structure

| Period | Timer | Duration | Notes |
|---|---|---|---|
| AUTO | 0:20 → 0:00 | 20 sec | No driver input. Both HUBs active. |
| TRANSITION SHIFT | 2:20 → 2:10 | 10 sec | TELEOP starts. Both HUBs still active. HUB light chase indicates which goes inactive next. |
| SHIFT 1 | 2:10 → 1:45 | 25 sec | Winner of AUTO = inactive HUB. Loser = active HUB. |
| SHIFT 2 | 1:45 → 1:20 | 25 sec | Alternates from SHIFT 1. |
| SHIFT 3 | 1:20 → 0:55 | 25 sec | Same pattern as SHIFT 1. |
| SHIFT 4 | 0:55 → 0:30 | 25 sec | Same pattern as SHIFT 2. |
| END GAME | 0:30 → 0:00 | 30 sec | Both HUBs active. Tower protection rule (G420) in effect. |

**Total match time: 2 minutes 40 seconds.**

### HUB Status Logic

- Alliance that scores **more FUEL in AUTO** → their HUB goes **inactive in SHIFT 1**
- If tied in AUTO, FMS randomly assigns
- FMS broadcasts which alliance won AUTO to all OPERATOR CONSOLEs at TELEOP start
- HUB status alternates each SHIFT until END GAME
- Scoring into an inactive HUB earns **0 points**
- Assessment continues for **3 seconds after timer hits 0:00** (both AUTO and TELEOP)

**Code implication:** Read FMS game data at TELEOP start to determine HUB status. Build a state machine or mode flag around this. Display active/inactive status on the driver dashboard.

```java
// FMS game data is accessible via DriverStation.getInstance().getGameSpecificMessage()
// Returns a String indicating which alliance's HUB is inactive in SHIFT 1
// Use match timer thresholds to calculate current SHIFT and derive HUB status
```

---

## Scoring

### Point Values

| Action | AUTO pts | TELEOP pts |
|---|---|---|
| FUEL in **active** HUB | 1 | 1 |
| FUEL in **inactive** HUB | 0 | 0 |
| TOWER Level 1 (robot off ground/TOWER BASE) | 15 | 10 |
| TOWER Level 2 (BUMPERS fully above LOW RUNG) | — | 20 |
| TOWER Level 3 (BUMPERS fully above MID RUNG) | — | 30 |

Level 1 in AUTO and TELEOP are separate — a robot can earn both. A robot may only earn points for one LEVEL in TELEOP.

### Ranking Points (Regionals/Districts)

| RP | Condition | Threshold |
|---|---|---|
| ENERGIZED RP | FUEL scored in active HUB ≥ threshold | 100 (regionals/districts) |
| SUPERCHARGED RP | FUEL scored in active HUB ≥ threshold | 360 (regionals/districts) |
| TRAVERSAL RP | Total TOWER points ≥ threshold | 50 (regionals/districts) |
| Win | More match points | 3 RP |
| Tie | Equal match points | 1 RP |

District Championship and FIRST Championship thresholds announced in Team Updates (TBA).

### TOWER Climbing Criteria

To qualify for a LEVEL, robot must:
- **Level 1:** No longer touching carpet OR TOWER BASE
- **Level 2:** BUMPER covers completely above LOW RUNG
- **Level 3:** BUMPER covers completely above MID RUNG

Robot must also be contacting at least one RUNG and/or UPRIGHT, and may only additionally contact: TOWER WALL, support structure, FUEL, or another robot.

TOWER points assessed 3 seconds after TELEOP ends OR when all robots stop, whichever is first.

---

## Field Elements and Dimensions

### Field Size
- 317.7 in × 651.2 in (~26.4 ft × 54.3 ft), low-pile carpet

### SCORING ELEMENT: FUEL
- 5.91 in (15.0 cm) diameter high-density foam ball
- Weight: 0.448–0.500 lb
- 504 FUEL staged per match total
- Up to 8 may be preloaded into each robot (48 max across 3 robots)

### HUB (1 per alliance)
- 47 × 47 in rectangular prism
- 41.7 in hexagonal top opening
- Front edge of opening: **72 in off carpet**
- Located 158.6 in from ALLIANCE WALL
- 4 exits at base distribute FUEL back into NEUTRAL ZONE
- DMX light bars indicate active (alliance color solid) / inactive (off) status
- AprilTags on all 4 faces (IDs vary by alliance/face), centers at 44.25 in off floor

### TOWER (1 per alliance)
- 49.25 in wide × 45 in deep × 78.25 in tall
- Integrated into ALLIANCE WALL between DS2 and DS3
- TOWER BASE: 39 in wide × 45.18 in deep, sits on floor
- UPRIGHTS: 72.1 in tall, 32.25 in apart (inner distance)
- RUNGS: 1.25 in Sch 40 pipe (1.66 in OD)
  - LOW RUNG center: **27.0 in off floor**
  - MID RUNG center: **45.0 in off floor**
  - HIGH RUNG center: **63.0 in off floor**
  - Rung-to-rung spacing: **18.0 in center-to-center**
- AprilTags on TOWER WALL (IDs 15, 16, 31, 32), centers at 21.75 in off floor

### BUMP (4 total, 2 per alliance side)
- 73 × 44.4 in footprint, **6.513 in tall**
- HDPE ramps at 15° angle (orange peel texture)
- Alliance colored
- Located on either side of each HUB

### TRENCH (4 total)
- 65.65 in wide × 47.0 in deep × 40.25 in tall
- **Drive-under clearance: 50.34 in wide × 22.25 in tall** ← critical for robot height design
- Extends from guardrail to BUMP
- AprilTags on top surface of arm (IDs 1, 6, 7, 12, 17, 22, 23, 28), centers at 35 in off floor, one facing ALLIANCE ZONE, one facing NEUTRAL ZONE

### DEPOT (2 total, 1 per alliance)
- 42 × 27 in, along ALLIANCE WALL
- 24 FUEL staged per match

### OUTPOST (1 per alliance)
- Human player station with CHUTE and CORRAL
- 24 FUEL staged in CHUTE per match
- Human players can feed FUEL to robots or score directly into HUB
- AprilTags (IDs 13, 14, 29, 30), centers at 21.75 in off floor

### AprilTag Summary (36h11 family, IDs 1–32)
- HUB: IDs 2, 3, 4, 5 (blue) and 8, 9, 10, 11 / 18, 19, 20, 21 / 24, 25, 26, 27 — 4 faces × 2 tags
- TOWER WALL: IDs 15, 16 (red), 31, 32 (blue)
- OUTPOST: IDs 13, 14 (red), 29, 30 (blue)
- TRENCH: IDs 1, 6, 7, 12 (red side), 17, 22, 23, 28 (blue side)
- All tags: 8.125 in square, mounted centered on 10.5 in polycarbonate panel

### Zone Definitions
- **ALLIANCE ZONE:** ~360 in × 134 in, includes ALLIANCE WALL, OUTPOST, TOWER WALL
- **NEUTRAL ZONE:** 283 in × 317.7 in, between BUMPS/TRENCHES/HUBS, includes CENTER LINE
- **ROBOT STARTING LINE:** ALLIANCE colored line at edge of ALLIANCE ZONE, in front of two BUMPS and HUB
- **CENTER LINE:** White line bisecting NEUTRAL ZONE

---

## Key Game Rules (In-Match)

### AUTO Rules
- **G401:** Drivers cannot touch controls in AUTO. MINOR FOUL + YELLOW CARD.
- **G402:** Robots must stay on their side of CENTER LINE at match start.
- **G403:** Robot fully across CENTER LINE may not contact opponent robot. MAJOR FOUL.

### FUEL / Scoring Rules
- **G404:** Cannot use FUEL to ease/amplify field element challenges (no launching at opponents, no using FUEL to boost climbs, no blocking TOWER access with FUEL). MAJOR FOUL.
- **G405:** Cannot intentionally eject FUEL from field. MINOR FOUL (REPEATED = MAJOR FOUL).
- **G406:** Cannot damage FUEL. VERBAL WARNING → MAJOR FOUL if repeated.
- **G407:** Robot BUMPERS must be partially or fully in own ALLIANCE ZONE to launch FUEL into HUB. **MAJOR FOUL.** Robots cannot shoot from NEUTRAL ZONE.
- **G408:** Cannot catch or redirect FUEL coming out of HUB exits until it contacts something else first. Sitting under exit to collect = MAJOR FOUL. MINOR FOUL otherwise.

### Robot Rules (In-Match)
- **G409:** Robot must be safe — no contacting outside field, no BUMPER detachment, no exposed perimeter corners, etc. DISABLED.
- **G410:** Extensions may not lift BUMPERS out of BUMPER ZONE. MINOR FOUL.
- **G411:** Cannot damage field elements. VERBAL WARNING → DISABLED.
- **G412:** Cannot grab, grasp, attach to, or suspend from field elements (exception: RUNGS and UPRIGHTS are legal to grab for climbing). MAJOR FOUL + YELLOW CARD.
- **G413:** Cannot exceed horizontal (R105, R106) or vertical (R107) expansion limits. MAJOR FOUL. Exceptions for momentary non-strategic flex or visible damage.
- **G414:** Cannot fully support an alliance partner's weight for climbing. Supported robot loses TOWER points.

### Robot Interaction Rules
- **G415:** No protrusions outside ROBOT PERIMETER (except BUMPERS) contacting inside opponent's ROBOT PERIMETER. MINOR FOUL.
- **G416:** No damaging or functionally impairing opponent robot by initiating interior contact. MAJOR FOUL + YELLOW CARD (or RED CARD if opponent cannot drive).
- **G417:** No deliberate tipping or entanglement. MAJOR FOUL + YELLOW CARD (or RED CARD if continuous/opponent cannot drive).
- **G418:** PIN limit is 3 seconds. Reset requires 72 in separation held for 3+ seconds. Additional MAJOR FOUL per 3 seconds of continued PIN.
- **G419:** 2+ robots may not cooperatively close off major game elements (both TRENCHes, both BUMPs, all SCORING ELEMENTS, opponent TOWER access). MAJOR FOUL per 3 seconds.
- **G420:** Cannot contact opponent robot touching opponent TOWER in last 30 seconds (END GAME), regardless of who initiates. MAJOR FOUL + opponent automatically awarded Level 3 TOWER points.

### Human Player Rules
- Human Players operate from OUTPOST AREA or ALLIANCE AREA
- Can deliver FUEL to robots or score directly into HUB from CHUTE
- **G421:** Must stay in designated area. MINOR FOUL per violation.

---

## Robot Construction Constraints

### Size and Weight
| Parameter | Limit |
|---|---|
| Max weight (excl. bumpers/battery) | 115 lb |
| Max STARTING CONFIGURATION perimeter | 110 in |
| Max STARTING CONFIGURATION height | 30 in |
| Max horizontal extension in-match | 12 in beyond ROBOT PERIMETER (R105) |
| Extension direction limit | One direction at a time (R106) |
| Max total height in-match | 30 in (R107) — exception during climbing |

**TRENCH clearance: 22.25 in tall × 50.34 in wide.** If robot exceeds 22.25 in height with mechanisms deployed, it cannot use TRENCH routes.

### Key Construction Rules
- **R101:** ROBOT PERIMETER must be fixed, non-articulated elements in STARTING CONFIGURATION
- **R102:** No parts outside vertical projection of ROBOT PERIMETER in STARTING CONFIGURATION
- **R103:** 115 lb weight limit (BUMPERS, battery, event location tags excluded)
- **R104:** Max 110 in perimeter, 30 in tall at start
- **R105/R106/R107:** 12 in horizontal (one direction), 30 in vertical extension limits
- **R201:** No carpet-damaging traction (no metal, sandpaper, studs, cleats, hook-loop)
- **R302:** All MAJOR MECHANISMS must be designed and built after Kickoff (Jan 10, 2026)
- **R303:** Software created before Kickoff only allowed if source was publicly posted before Kickoff

### Preload Rule
- Max 8 FUEL fully supported by robot at match start (G303-I)
- Unstaged FUEL goes to NEUTRAL ZONE

### BUMPER Rules (Summary)
- Required, must cover near-full perimeter
- Minimum 2.25 in foam depth × 4.5 in tall
- Rigid fastening (no tape, no zip ties)
- Must display ALLIANCE color and team number
- Must stay in BUMPER ZONE (defined in R405): roughly 0–7.5 in off ground

---

## Autonomous Programming Notes

### Starting Position
- BUMPERS must overlap ROBOT STARTING LINE (G303-D)
- Robot must not contact BUMP at match start (G303-E)
- Teams encouraged to have multiple start positions (Head REFEREE note)

### AUTO Objectives (in priority order, strategy-dependent)
1. Score FUEL into HUB (1 pt each, both HUBs active)
2. Optionally climb TOWER to Level 1 for 15 pts
3. Manage how many FUEL you score relative to opponent (see HUB Shift strategy)

### Vision/Localization
- AprilTags (36h11, IDs 1–32) available on HUB, TOWER WALL, OUTPOST, TRENCH
- Use PhotonVision or Limelight with AprilTag pipeline
- HUB tags at 44.25 in height; TOWER WALL/OUTPOST tags at 21.75 in height; TRENCH tags at 35 in height
- HUB front edge is 72 in off carpet at the opening

### PathPlanner Notes
- ALLIANCE ZONE shooting constraint: BUMPERS must be in ALLIANCE ZONE to score (G407)
- Do not plan paths that put robot in NEUTRAL ZONE when it needs to shoot
- CENTER LINE crossing in AUTO triggers G403 if robot contacts opponent — path must not cross unless robot stays on its half
- TRENCH routing only viable if robot ≤ 22.25 in tall with mechanisms folded

---

## Penalties Reference

| Penalty | Points to opponent | Notes |
|---|---|---|
| MINOR FOUL | +5 | Per instance |
| MAJOR FOUL | +15 | Per instance |
| YELLOW CARD | — | Warning; 2nd = RED CARD |
| RED CARD | — | DISQUALIFIED (0 pts, 0 RP in quals) |
| DISABLED | — | Robot deactivated for remainder of match |

---

## Strategy Notes (for code context)

**HUB Shift awareness:** FMS game data at TELEOP start tells you which alliance won AUTO. Map this to a `hubActive` boolean updated each SHIFT based on match timer. Make this visible on dashboard.

**SUPERCHARGED RP math:** 360 FUEL at 1 pt each across ~100 effective scoring seconds = 3.6 FUEL/sec alliance throughput. Extremely aggressive. ENERGIZED RP at 100 FUEL is realistic target.

**TRAVERSAL RP math:** Three robots at Level 2 = 60 pts (threshold 50, cleared). Two Level 3 + one Level 1 TELEOP = 70 pts. Minimum viable: two Level 2 + anything = 40 pts (fails). Need at minimum two Level 2 climbs.

**G408 catch rule:** HUB exits push FUEL into NEUTRAL ZONE. Cannot position robot under exit to collect directly. Collect from floor after FUEL contacts carpet.

**G420 protection:** During END GAME, if an opponent touches your robot while it's on the TOWER, you get Level 3 automatically. Design climber to be clearly off the ground and unambiguously attached.

---

## Audio Cues (FMS)

| Event | Timer | Sound |
|---|---|---|
| Match start | 0:20 | Cavalry Charge |
| AUTO ends | 0:00 AUTO | Buzzer |
| TELEOP starts | 2:20 | 3 Bells |
| Each SHIFT change | 2:10, 1:45, 1:20, 0:55 | POWER UP Linear Popping |
| END GAME starts | 0:30 | Steam Whistle |
| Match ends | 0:00 | Buzzer |

Audio cues are courtesy only — use FMS data and match timer for code logic, not audio.

---

*Source: 2026 FRC Game Manual REBUILT™ presented by Haas, Version TU16. Check firstinspires.org for Team Updates that may change thresholds or rules.*
